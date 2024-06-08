package org.hw;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import static org.apache.zookeeper.AddWatchMode.PERSISTENT_RECURSIVE;

public class ZooApp implements Watcher {
    public static final String MAIN_ZNODE_NAME = "/a";
    private final ZooKeeper zooKeeper;
    private final String externalAppCmd;
    private Process externalAppProcess;
    private boolean exists;

    public ZooApp(String connectString, int sessionTimeout, String externalAppProcess) {
        this.externalAppCmd = externalAppProcess;
        try {
            zooKeeper = new ZooKeeper(connectString, sessionTimeout, this);
            exists = zooKeeper.exists(MAIN_ZNODE_NAME, null) != null;
            zooKeeper.addWatch(MAIN_ZNODE_NAME, this, PERSISTENT_RECURSIVE);
        } catch (IOException | KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!exists) {
            System.out.println("Waiting for creation of the znode " + MAIN_ZNODE_NAME + "...");
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input 'tree' to see the znode tree structure or 'exit' to exit");
        while (true) {
            String cmd = scanner.nextLine();
            if(cmd.equals("tree")){
                try {
                    printTree();
                } catch (InterruptedException | KeeperException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(cmd.equals("exit")){
                close();
                break;
            }
            else{
                System.out.println("Unknown command");
            }
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        //System.out.println("Process entered. " + watchedEvent.getType().toString());
        if (watchedEvent.getType() == Watcher.Event.EventType.NodeCreated && MAIN_ZNODE_NAME.equals(watchedEvent.getPath())) {
            exists = true;
            System.out.println("Node " + MAIN_ZNODE_NAME + " created.");
            startExternalApp();
        }

        if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted && MAIN_ZNODE_NAME.equals(watchedEvent.getPath())) {
            exists = false;
            System.out.println("Node " + MAIN_ZNODE_NAME + " deleted.");
            stopExternalApp();
        }

        if (exists && !MAIN_ZNODE_NAME.equals(watchedEvent.getPath())) {
            try {
                List<String> children;
                ArrayDeque<String> queue = new ArrayDeque<>();
                queue.add(MAIN_ZNODE_NAME);
                int cnt=0;
                while(!queue.isEmpty()){
                    String currPath=queue.pop();
                    children = zooKeeper.getChildren(currPath, null);
                    cnt+=children.size();
                    for(String child: children){
                        queue.add(currPath+"/"+child);
                    }
                }
                System.out.println("Children state changed for " + MAIN_ZNODE_NAME + ", current number of children: " + cnt);
            } catch (KeeperException.NoNodeException e) {
                //System.out.println("Node " + MAIN_ZNODE_NAME + " does not exist.");
            } catch (KeeperException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void startExternalApp() {
        try {
            externalAppProcess = Runtime.getRuntime().exec(externalAppCmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopExternalApp() {
        if (externalAppProcess != null) {
            externalAppProcess.destroy();
        }
    }

    private void printTree() throws InterruptedException, KeeperException {
        if(!exists){
            System.out.println(MAIN_ZNODE_NAME +" does not exist.");
        }else{
            System.out.println("---------begin---------");
            List<String> children;
            Stack<String> stack = new Stack<>();
            stack.push(MAIN_ZNODE_NAME);
            while(!stack.empty()){
                String currPath=stack.pop();
                int cnt = currPath.length() - currPath.replace("/", "").length();
                System.out.println(" ".repeat((cnt-1)*3)+"|__"+currPath.substring(currPath.lastIndexOf('/')+1));
                children = zooKeeper.getChildren(currPath, null);
                for(String child: children){
                    stack.push(currPath+"/"+child);
                }
            }
            System.out.println("----------end-----------");
        }
    }

    private void close(){
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Disconnected from Zookeeper. Closing...");
    }
}
