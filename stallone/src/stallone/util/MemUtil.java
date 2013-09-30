package stallone.util;


/**
 * @author  Martin Senne
 */
public class MemUtil {


    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * Total memory currently occupied by the virtual machine. Can change during runtime. totalMemory &lt; maxMemory.
     *
     * @return  total memory
     */
    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    /**
     * Get maximum size of avaiable space by the virtual machine. Corresponds with -Xmx &lt;mem&gt;.
     *
     * @return  maximum available memory
     */
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getDefinitelyOccupied() {
        return getTotalMemory() - getFreeMemory();
    }

    public static long getMaxMemAvailable() {
        return getMaxMemory() - getDefinitelyOccupied();
    }
}


/*
 * System.out.println( "Free : " + Runtime.getRuntime().freeMemory()/(1024*1024) +" MB." );
 * System.out.println( "Total: " + Runtime.getRuntime().totalMemory()/(1024*1024) +" MB.");
 * System.out.println( "Max  : " + Runtime.getRuntime().maxMemory()/(1024*1024) +" MB.");
 *
 * long s = 2024 * 1024 * 256;
 * int[] lustig = new int[ (int)s ];
 *
 * System.out.println( "Free : " + Runtime.getRuntime().freeMemory()/(1024*1024) +" MB." );
 * System.out.println( "Total: " + Runtime.getRuntime().totalMemory()/(1024*1024) +" MB.");
 * System.out.println( "Max  : " + Runtime.getRuntime().maxMemory()/(1024*1024) +" MB.");
 *
 *
 *
 * System.out.println( "Free : " + Runtime.getRuntime().freeMemory()/(1024*1024) +" MB." );
 * System.out.println( "Total: " + Runtime.getRuntime().totalMemory()/(1024*1024) +" MB.");
 * System.out.println( "Max  : " + Runtime.getRuntime().maxMemory()/(1024*1024) +" MB.");
 */
