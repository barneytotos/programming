package base;

import base.p2p.file.FakeFile;
import base.p2p.file.FakeP2PFile;
import base.p2p.file.P2PFile;
import base.p2p.peer.FakePeer;
import base.p2p.tracker.FakeTracker;
import base.p2p.tracker.Tracker;
import base.p2p.transfer.FakeTransfer;
import base.p2p.transfer.Transfer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) { launch(args); }
    public Stage getPrimaryStage() { return primaryStage; }

    private Stage primaryStage;
    private BorderPane rootLayout;

    private ObservableList<P2PFile> localFiles = FXCollections.observableArrayList();
    private ObservableList<Tracker> knownTrackers = FXCollections.observableArrayList();
    private ObservableList<Transfer> ongoingTransfers = FXCollections.observableArrayList();

    /* TODO add sample data */
    public Main() {
        localFiles.add(new FakeP2PFile(new FakeFile("fakeLoc"), 23, "233.421.151.51:322"));
        knownTrackers.add(new FakeTracker("233.421.151.51:322"));
        ongoingTransfers.add(new FakeTransfer(localFiles.get(0), new FakePeer("2.4.12.2:5")));
    }

    @Override public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("p2p-gui");

        initTheWindow();
    }

    private void initTheWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/TheWindow.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e) { e.printStackTrace(); }
    }


}