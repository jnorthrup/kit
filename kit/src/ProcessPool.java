import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Selector;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ProcessPool {
    private static final int CORE_POOL_SIZE = 8;
    private Map<String, ReadableByteChannel> outMap;
    private boolean finished = false;

    protected void finalize() {
        finished = true;
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();  //is this informative enough?
        }
    }

    class ProcessOutput {
        String out, err;

        Runnable selector = new Runnable() {
            public void run() {

                try {
                    Selector sel = Selector.open();

                    while (!finished) {
                        sel.selectedKeys();
                    }
                } catch (IOException e) {
                    e.printStackTrace();  //is this informative enough?
                }

            }
        };
    }


    class ProcessTag {
        String tag;
        ProcessBuilder proc;
        private WritableByteChannel output;
        int exit;


        public ProcessTag(String tag, ProcessBuilder proc) {
            this.tag = tag;
            this.proc = proc;
        }


        public Runnable getRunnable() {
            return new Runnable() {

                public void run() {
                    try {
                        proc.redirectErrorStream(true);

                        Process process = proc.start();
                        InputStream in = process.getInputStream();
                        ReadableByteChannel rrr = Channels.newChannel(in);
                        outMap.put(tag, rrr);
                        exit = process.waitFor();
                    } catch (IOException e) {
                        e.printStackTrace();  //is this informative enough?
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //is this informative enough?
                    }
                }
            };
        }


    }

    static long counter = System.currentTimeMillis();

    String addProcess(String tag, String cmd) throws UnknownHostException {
        counter++;
        new ProcessBuilder(cmd);
        return "process-" + java.net.Inet4Address.getLocalHost().getHostName() + " $ " + cmd;

    }

    ProcessPool() {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);
        outMap = new HashMap<String, ReadableByteChannel>();
    }

    void onFinish(ProcessTag pt, String Output) {
        System.err.println(pt.tag + " exit code:" + pt.exit);
        // do something
    }

    void testProcessPool() {
        new ProcessBuilder("sleep 300");
    }

}

