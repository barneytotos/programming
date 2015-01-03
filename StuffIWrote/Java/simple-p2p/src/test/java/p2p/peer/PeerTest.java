package p2p.peer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import p2p.Swarm;
import p2p.Tracker;
import p2p.exceptions.MetadataMismatchException;
import p2p.exceptions.SwarmNotFoundException;
import p2p.file.P2PFile;
import p2p.file.P2PFileMetadata;

import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.nio.file.FileSystemException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PeerTest {

    static final String SAMPLE_FILENAME = "asdfile";

    Peer peer, peer2;
    Tracker tracker;
    P2PFile sampleP2PFile;

    /* "The ExpectedException rule allows you to verify
        that your code throws a specific exception."    */
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        tracker = new Tracker();
        tracker.start();

        InetSocketAddress trackerAddr = tracker.getInetSocketAddr();

        peer = new Peer();
        peer.setTracker(trackerAddr);

        peer2 = new Peer();
        peer2.setTracker(trackerAddr);

        sampleP2PFile = new P2PFile(SAMPLE_FILENAME, trackerAddr);
    }

    /**
     * register sample file with tracker
     */
    void shareSampleFile() {
        try { peer.shareFile(SAMPLE_FILENAME); }
        catch (FileNotFoundException | FileSystemException e) { e.printStackTrace(); }
    }

    /**
     * peer knows it is sharing the file
     */
    @Test
    public void testShareFilePeerSeeding() throws Exception {
        shareSampleFile();
        assertTrue(peer.completeAndSeeding.contains(sampleP2PFile));
    }

    /**
     * tracker maintains swarm for file
     */
    @Test
    public void testShareFileTrackerTrackingName() throws Exception {
        shareSampleFile();
        synchronized (tracker.swarmsByFilename) {
            tracker.swarmsByFilename.wait();
        }
        assertTrue(tracker.isTrackingFilename(SAMPLE_FILENAME));
    }

    /**
     * tracker's swarm is correct
     */
    @Test
    public void testShareFileTrackerSwarm() throws Exception {
        shareSampleFile();
        P2PFileMetadata trueMeta = sampleP2PFile.metadata;
        ConcurrentHashMap<String, Swarm> swarmMap = tracker.swarmsByFilename;

        // wait for the tracker to create a swarm in the map
        synchronized (swarmMap) { swarmMap.wait(); }

        assertEquals(1, swarmMap.size());
        Swarm sampleSwarm = swarmMap.get(SAMPLE_FILENAME);
        assertEquals(1, sampleSwarm.numSeeders());
        P2PFileMetadata savedMeta = sampleSwarm.pFileMetadata;
        assertEquals(trueMeta, savedMeta);


        // LowPriorityTODO (at some point, figure out whether to fix this)
        // this fails because saved IP is "internal" vrsn
        // I'm not sure whether to hack some fix to this or not because
        // it really depends on whether the tracker's socket.getRemoteAddress()
        // in some sort of "production" environment would see the internal or
        // external address, and I have no idea what happens in that situation.

//        InetAddress myExternalIPAddr = Common.findMyIP();
//        Iterator<InetSocketAddress> it = sampleSwarm.seeders.iterator();
//        InetSocketAddress savedSockAddrOfPeerInSwarm = it.next();
//        InetAddress savedIPAddrOfPeerInSwarm = savedSockAddrOfPeerInSwarm.getAddress();

//        assertEquals(myExternalIPAddr, savedIPAddrOfPeerInSwarm);
    }

    @Test
    public void peer1SharePeer2List() throws Exception {
        shareSampleFile();
        SortedSet<P2PFileMetadata> receivedFileList = peer2.listSavedTracker();
        SortedSet<P2PFileMetadata> trueFileList = new TreeSet<>();
        trueFileList.add(sampleP2PFile.metadata);
        assertEquals(trueFileList, receivedFileList);
    }

    @Test
    public void testGetSeederIPsForFile() throws Exception {
        shareSampleFile();
        Set<InetSocketAddress> peerIPs =
                peer2.getSeedersForFile(sampleP2PFile.metadata, peer2.trkAddr);
        InetSocketAddress peerSocketAddr =
                new InetSocketAddress(peer.externalIPAddr, peer.getListeningPort());
        Set<InetSocketAddress> trueSet = new HashSet<>();
        trueSet.add(peerSocketAddr);
        assertEquals(trueSet, peerIPs);
    }

    @Test
    public void testGetSeederIPsForIncorrectDigest() throws Exception {
        thrown.expect(MetadataMismatchException.class);
        shareSampleFile();

        P2PFileMetadata m = sampleP2PFile.metadata.clone();
        m.getSha2Digest()[2] = (byte) 234;
        peer2.getSeedersForFile(m, peer2.trkAddr);
    }

    @Test
    public void testGetSeederIPsForUnknownFilename() throws Exception {
        thrown.expect(SwarmNotFoundException.class);
        shareSampleFile();
        P2PFileMetadata m = sampleP2PFile.metadata.clone();
        m.setFilename("non-existent file");
        peer2.getSeedersForFile(m, peer2.trkAddr);
    }

    // TODO implement the functionality
    @Test
    public void peer1SharePeer2Download() throws Exception {
        shareSampleFile();
        P2PFile dldFile = peer2.downloadFromSavedTracker(sampleP2PFile.metadata);
        assertEquals(sampleP2PFile, dldFile);
    }

    @Test
    public void peer1SharePeer2ListThenDownload() throws Exception {
        shareSampleFile();
        SortedSet<P2PFileMetadata> receivedFileList = peer2.listSavedTracker();
        P2PFile dldFile = peer2.downloadFromSavedTracker(receivedFileList.first());
        assertEquals(sampleP2PFile, dldFile);
    }
}