/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat;

import java.util.Arrays;
import java.util.List;
import static stallone.api.API.*;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleList;
import stallone.util.CommandLineParser;

/**
 *
 * @author noe
 */
public class EventBinningCorrelator
{

    private IDoubleArray lagtimes;
    private IDoubleArray averageWidths;
    private double samplingTime = 1;
    private IDoubleArray correlation;
    private IDoubleArray weights;
    private double sum = 0;
    private double sumOfWeights = 0;

    /**
     *
     * @param _lagtimes
     * @param _averageWidths
     * @param samplingTime the time step at which t is advanced when calculating
     * the average < x * y >_t
     */
    public EventBinningCorrelator(IDoubleArray _lagtimes, IDoubleArray _averageWidths, double _samplingTime)
    {
        if (_lagtimes.size() != _averageWidths.size())
        {
            throw (new IllegalArgumentException("Number of lagtimes does not match number of average widths in correlate."));
        }

        lagtimes = _lagtimes;
        averageWidths = _averageWidths;
        samplingTime = _samplingTime;
        correlation = doublesNew.array(lagtimes.size());
        weights = doublesNew.array(lagtimes.size());
    }

    public static String getUsageString()
    {
        String res = "EventCorrelator" + "\n"
                + "Correlates the given columns of all files passed."
                + "-i <trajectorie(s)>" + "\n"
                + "\n"
                + "-columns <time> <D> <A>" + "\n"
                + "-samplingtime <dt>" + "\n"
                + "-maxtime <maxy>" + "\n"
                + "-latmult <mult>" + "\n"
                + "-windowfraction <fw>" + "\n"
                + "-subtractmean" + "\n";

        return res;
    }

    public static CommandLineParser parseArguments(String[] args)
    {
        CommandLineParser parser = new CommandLineParser();

        // input
        parser.addStringArrayCommand("i", true);

        parser.addCommand("columns", true);
        parser.addIntArgument("columns", true);
        parser.addIntArgument("columns", true);
        parser.addIntArgument("columns", true);

        parser.addIntCommand("maxtime", true);
        parser.addDoubleCommand("lagmult", true);

        parser.addIntCommand("samplingtime", true);

        parser.addDoubleCommand("windowfraction", true);

        parser.addCommand("subtractmean", true);

        // parse
        if (!parser.parse(args))
        {
            throw (new IllegalArgumentException("Parsing error!"));
        }

        return parser;
    }

    private static IDoubleArray cumulate(IDoubleArray data)
    {
        IDoubleArray res = doublesNew.array(data.size() + 1);
        res.set(0, 0);
        for (int i = 1; i < res.size(); i++)
        {
            res.set(i, res.get(i - 1) + data.get(i - 1));
        }
        return res;
    }

    class Window
    {
        public IDoubleArray times;
        public IDoubleArray data;
        public double width;
        public double t, tl, tr;
        public int l=0, r=0; // first event index included and first event index after window [t-width, t+width]

        public Window(IDoubleArray _times, IDoubleArray _data, double _width)
        {
            this.times = _times;
            this.data = _data;
            this.width = _width;
        }

        /**
         * Positions the window to time t.
         */
        public void init(double _t)
        {
            this.t = _t;
            this.tl = t-width/2.0;
            this.tr = t+width/2.0;

            l=0;
            r=0;
            advance(0);
        }

        public void advance(double dt)
        {
            this.t += dt;
            this.tl = t-width/2.0;
            this.tr = t+width/2.0;

            // move left index
            while (l<times.size())
            {
                if (times.get(l) < tl)
                    l++;
                else
                    break;
            }

            // move right index
            while (r<times.size())
            {
                if (times.get(r) < tr)
                    r++;
                else
                    break;
            }
        }

        public boolean isEmpty()
        {
            return (l==r);
        }

        public boolean endOfData()
        {
            return (l>=times.size());
        }
    }


    public double correlate(IDoubleArray time, IDoubleArray data, double tau, double windowSize)
    {
        if (time.size() != data.size())
        {
            throw (new IllegalArgumentException("Number of time points does not match number of data points in correlate."));
        }

        // cumulate data
        IDoubleArray cumdata = cumulate(data);

        Window w1 = new Window(time, data, windowSize);
        w1.init(windowSize/2.0);

        Window w2 = new Window(time, data, windowSize);
        w2.init(windowSize/2.0 + tau);

        // estimate
        double sum = 0;
        double count = 0;

        while(!w2.endOfData())
        {
            /*
                if (tau == 0)
                {
                    System.out.println("t = "+w1.t+"\t times ["+w1.tl+","+w1.tr+"] -> ["+w2.tl+","+w2.tr+"]");
                    System.out.println("  w1=("+w1.l+", "+w1.r+") w2="+w2.l+", "+w2.r);
                }*/

            // update convolution sums
            if ((!w1.isEmpty()) && (!w2.isEmpty()))
            {
                double x1 = (cumdata.get(w1.r) - cumdata.get(w1.l))/((double)(w1.r-w1.l));
                double x2 = (cumdata.get(w2.r) - cumdata.get(w2.l))/((double)(w2.r-w2.l));
                sum += x1*x2;
                count += 1.0;

                /*if (tau == 0)
                {
                    System.out.println("t = "+w1.t+"\t times ["+w1.tl+","+w1.tr+"] -> ["+w2.tl+","+w2.tr+"]");
                    System.out.println("  w1=("+w1.l+", "+w1.r+") w2="+w2.l+", "+w2.r);
                    System.out.println("  x1= "+x1+"  x2= "+x2);
                }*/
            }

            // advance window by dt.
            w1.advance(samplingTime);
            w2.advance(samplingTime);
        }

        return sum / count;
    }

    /**
     * Correlates the given set of events on the tau-grid specified
     *
     * @param time
     * @param data
     * @return the autocorrelations of the data on the tau grid specified
     */
    public IDoubleArray correlate(IDoubleArray time, IDoubleArray data)
    {
        IDoubleArray res = doublesNew.array(lagtimes.size());

        for (int i = 0; i < res.size(); i++)
        {
            res.set(i, correlate(time, data, lagtimes.get(i), averageWidths.get(i)));
        }

        return res;
    }

    /**
     * Adds data set to a cumulative correlation
     *
     * @param time
     * @param data
     */
    public void add(IDoubleArray time, IDoubleArray data)
    {
        IDoubleArray c = correlate(time, data);
        for (int i = 0; i < c.size(); i++)
        {
            if (!Double.isNaN(c.get(i)))
            {
                correlation.set(i, correlation.get(i) + data.size() * c.get(i));
                weights.set(i, weights.get(i) + data.size());
            }
        }

        sum += doubles.sum(data);
        sumOfWeights += time.size();
    }

    public IDoubleArray getCorrelation()
    {
        IDoubleArray res = doublesNew.array(correlation.size());
        for (int i = 0; i < res.size(); i++)
        {
            res.set(i, correlation.get(i) / weights.get(i));
        }
        return res;
    }

    public IDoubleArray getCorrelationMeanFree()
    {
        IDoubleArray res = doublesNew.array(correlation.size());
        double shift = (sum / sumOfWeights) * (sum / sumOfWeights);
        System.out.println("# square mean = "+shift);
        for (int i = 0; i < res.size(); i++)
        {
            res.set(i, (correlation.get(i) / weights.get(i)) - shift);
        }
        return res;
    }

    public void reset()
    {
        correlation.zero();
        weights.zero();
        sum = 0;
        sumOfWeights = 0;
    }

    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println(getUsageString());
            System.exit(0);
        }

        CommandLineParser parser = parseArguments(args);

        // load photon trajectories
        List<String> inputFiles = Arrays.asList(parser.getStringArray("i"));

        int timecol = parser.getInt("columns", 0);
        int col1 = parser.getInt("columns", 1);
        int col2 = parser.getInt("columns", 2);
        double lagtmult = parser.getDouble("lagmult",0);
        int maxtime = parser.getInt("maxtime", 0);
        int samplingtime = parser.getInt("samplingtime",0);
        //double windowfraction = parser.getDouble("windowfraction",0);
        boolean subtractMean = parser.hasCommand("subtractmean");


        IDoubleList lagtimes = doublesNew.listFrom(0, 1);
        while (lagtimes.get(lagtimes.size() - 1) < maxtime)
        {
            lagtimes.append((int) ((lagtimes.get(lagtimes.size() - 1) + 1) * lagtmult));
        }
        //IDoubleArray lagtimes = doublesNew.arrayFrom(1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000);
        IDoubleArray averageWidths = doublesNew.array(lagtimes.size());
        for (int i = 0; i < averageWidths.size(); i++)
        {
            averageWidths.set(i, 1.0);
            //averageWidths.set(i, Math.max(1.0, windowfraction * lagtimes.get(i)));
            //System.out.println(lagtimes.get(i)+"\t"+averageWidths.get(i));
        }
        //System.out.println();
        //System.exit(0);
        EventBinningCorrelator correlator = new EventBinningCorrelator(lagtimes, averageWidths, samplingtime);


        for (int i = 0; i < inputFiles.size(); i++)
        {
            IDoubleArray time = data.readColumn(inputFiles.get(i), timecol);
            IDoubleArray don = data.readColumn(inputFiles.get(i), col1);
            IDoubleArray acc = data.readColumn(inputFiles.get(i), col2);
            IDoubleArray E = doublesNew.array(don.size());
            for (int j = 0; j < E.size(); j++)
            {
                E.set(j, acc.get(j) / (don.get(j) + acc.get(j)));
            }

            correlator.add(time, E);
        }

        IDoubleArray corr = null;
        if (subtractMean)
        {
            corr = correlator.getCorrelationMeanFree();
        }
        else
        {
            corr = correlator.getCorrelation();
        }


        //IDoubleArray corr = correlator.correlate(time, E);

        for (int i = 0; i < lagtimes.size(); i++)
        {
            System.out.println(lagtimes.get(i) + "\t" + corr.get(i));
        }
    }
}
