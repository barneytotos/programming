#include <libssh2.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <stdio.h>
#include <ctype.h>

// connection
const char *username = "ethanp";
const char *password = "nuh-uh";
int sock, i, auth_pw = 0;
LIBSSH2_SESSION *session;
const char *fingerprint;
struct sockaddr_in sin;
unsigned long hostaddr;

// file transfer
const char *newpath = "/u/ethanp/ech2";
const char *scppath = "/u/ethanp/ech";
const char *local_file_path = "./ech";
LIBSSH2_CHANNEL *channel;
struct stat fileinfo;
char mem[1024];
size_t nread;
FILE *local;
char *ptr;
int rc;

int scp_send(const char *local_path, const char *remote_path) {
    /* Send a file via scp. The mode parameter must only have permissions */
    local = fopen(local_path, "rb");
    if (!local) {
        fprintf(stderr, "Can't local file %s\n", local_path);
        return -1;
    }
    stat(local_path, &fileinfo);
    printf("sending: %s, of size %d to: %s\n",
            local_path, (int)fileinfo.st_size, remote_path);
    channel = libssh2_scp_send(session, remote_path, fileinfo.st_mode & 0777,
                               (unsigned long)fileinfo.st_size);
    if (!channel) {
        char *errmsg;
        int errlen;
        int err = libssh2_session_last_error(session, &errmsg, &errlen, 0);
        fprintf(stderr, "Unable to open a session: (%d) %s\n", err, errmsg);
        scp_shutdown();
    }
    fprintf(stderr, "SCP session waiting to send file\n");
    do {
        nread = fread(mem, 1, sizeof(mem), local);
        if (nread <= 0) {
            /* end of file */
            break;
        }
        ptr = mem;
        do { /* write the same data over and over, until error or completion */
            rc = libssh2_channel_write(channel, ptr, nread);
            if (rc < 0) {
                fprintf(stderr, "ERROR %d\n", rc);
                break;
            }
            else { /* rc indicates how many bytes were written this time */
                ptr += rc;
                nread -= rc;
            }
        } while (nread);
    } while (1);
    fprintf(stderr, "File sent, sending EOF\n");
    libssh2_channel_send_eof(channel);
    fprintf(stderr, "Waiting for EOF\n");
    libssh2_channel_wait_eof(channel);
    fprintf(stderr, "Waiting for channel to close\n");
    libssh2_channel_wait_closed(channel);
    libssh2_channel_free(channel);
    channel = NULL;
}

int scp_retrieve(const char *path) { // path is the remote filename
    off_t got = 0;
    printf("requesting: %s\n", path);
    channel = libssh2_scp_recv(session, path, &fileinfo);
    if (!channel) {
        fprintf(stderr, "Unable to open a session: %d\n",
                libssh2_session_last_errno(session));
        char *err_msg;
        libssh2_session_last_error(session, &err_msg, NULL, 0);
        fprintf(stderr, "Error info: %s\n", err_msg);
        scp_shutdown();
    }
    while(got < fileinfo.st_size) {
        char mem[1024];
        int amount=sizeof(mem);
        if((fileinfo.st_size - got) < amount) {
            amount = fileinfo.st_size - got;
        }
        rc = libssh2_channel_read(channel, mem, amount);
        if(rc > 0) { write(1, mem, rc); }
        else if(rc < 0) {
            fprintf(stderr, "libssh2_channel_read() failed: %d\n", rc);
            break;
        }
        got += rc;
    }
    libssh2_channel_free(channel);
    channel = NULL;

}

int init(int argc, char *argv[]) {
    hostaddr = inet_addr("128.83.120.177");
    if (argc > 1) { password = argv[1]; }
    if (argc > 2) { username = argv[2]; }
    if (argc > 3) { hostaddr = inet_addr(argv[3]); }
    if (argc > 4) { scppath = argv[4]; }
    rc = libssh2_init(0);
    if (rc != 0) {
        fprintf (stderr, "libssh2 initialization failed (%d)\n", rc);
        return 1;
    }
    /* Ultra basic "connect to port 22 on localhost"
     * Your code must create the socket establishing the connection */
    sock = socket(AF_INET, SOCK_STREAM, 0);
    sin.sin_family = AF_INET;
    sin.sin_port = htons(22);
    sin.sin_addr.s_addr = hostaddr;
    if (connect(sock, (struct sockaddr*)(&sin),
            sizeof(struct sockaddr_in)) != 0) {
        fprintf(stderr, "failed to connect!\n");
        return -1;
    }
    /* Create a session instance */
    session = libssh2_session_init();
    if(!session) {
        fprintf(stderr, "couldn't create a libssh2_session\n");
        return -1;
    }
    /* ... start it up. This will trade welcome banners, exchange keys,
     * and setup crypto, compression, and MAC layers */
    rc = libssh2_session_handshake(session, sock);
    if(rc) {
        fprintf(stderr, "Failure establishing SSH session: %d\n", rc);
        return -1;
    }
    /* At this point we havn't yet authenticated.  The first thing to do
     * is check the hostkey's fingerprint against our known hosts Your app
     * may have it hard coded, may go to a file, may present it to the
     * user, that's your call */
    fingerprint = libssh2_hostkey_hash(session, LIBSSH2_HOSTKEY_HASH_SHA1);
    if (auth_pw) {  /* We could authenticate via password */
        if (libssh2_userauth_password(session, username, password)) {
            fprintf(stderr, "Authentication by password failed.\n");
            scp_shutdown();
        }
    } else {        /* Or by public key */
        if (libssh2_userauth_publickey_fromfile(session, username,
                            "/home/ethan/.ssh/id_rsa.pub",
                            "/home/ethan/.ssh/id_rsa",
                            password)) {
            fprintf(stderr, "\tAuthentication by public key failed\n");
            scp_shutdown();
        }
    }
    return 0;
}

 void scp_shutdown() {
    libssh2_session_disconnect(session, "Normal Shutdown, Thank you for playing");
    libssh2_session_free(session);
    close(sock);
    fprintf(stderr, "connection scp_shutdown\n");
    libssh2_exit();
    exit(0);
}
