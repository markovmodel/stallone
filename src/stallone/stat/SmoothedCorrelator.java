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
public class SmoothedCorrelator
{
    private IDoubleArray lagtimes;
    private IDoubleArray averageWidths;


    private IDoubleArray correlation;
    private IDoubleArray weights;
    private double sum = 0;
    private double sumOfWeights = 0;

    public SmoothedCorrelator(IDoubleArray _lagtimes, IDoubleArray _averageWidths)
    {
        if (_lagtimes.size() != _averageWidths.size())
        {
            throw (new IllegalArgumentException("Number of lagtimes does not match number of average widths in correlate."));
        }

        lagtimes = _lagtimes;
        averageWidths = _averageWidths;
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
                + "-maxtime <maxy>" + "\n"
                + "-subtractmean" + "\n"
                ;

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
        IDoubleArray res = doublesNew.array(data.size()+1);
        res.set(0,0);
        for (int i=1; i<res.size(); i++)
            res.set(i, res.get(i-1)+data.get(i-1));
        return res;
    }

    public static double correlate(IDoubleArray time, IDoubleArray data, double tau, double averageWidth)
    {
        if (time.size() != data.size())
        {
            throw (new IllegalArgumentException("Number of time points does not match number of data points in correlate."));
        }

        int i1 = 0, i2 = 0; // left window
        int j1 = 0, j2 = 0; // right window
        double sum = 0; // sum of products
        double count = 0; // number of products

        IDoubleArray cumdata = cumulate(data);

        double maxtime = time.get(time.size() - 1);
        // as long as there is enough space:
        while (maxtime - time.get(i1) >= tau)
        {
            // move end of left window
            i2 = i1;
            while (time.get(i2) - time.get(i1) <= averageWidth && i2 < time.size())
            {
                i2++;
                if (i2 >= time.size())
                {
                    break;
                }
            }

            // move beginning of right window
            while (time.get(j1) - time.get(i1) < tau
                    && maxtime - time.get(j1) >= tau)
            {
                j1++;
                if (j1 >= time.size())
                {
                    break;
                }
            }

            // move end of right window
            j2 = j1;
            if (j2 < time.size())
            {
                while (time.get(j2) - time.get(j1) <= averageWidth && j2 < time.size())
                {
                    j2++;
                    if (j2 >= time.size())
                    {
                        break;
                    }
                }
            }

            /*
            if (tau == 103247)
            {
                System.out.println("("+i1+","+i2+")\t("+j1+","+j2+")");
            }
            */

            // update autocorrelation
            double x1 = (cumdata.get(i2)-cumdata.get(i1))/(i2-i1);
            double x2 = (cumdata.get(j2)-cumdata.get(j1))/(j2-j1);
            sum += x1*x2;
            count += 1;

            // shift left window
            i1++;
            if (i1 >= time.size())
                break;
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
        for (int i=0; i<res.size(); i++)
            res.set(i, correlation.get(i)/weights.get(i));
        return res;
    }

    public IDoubleArray getCorrelationMeanFree()
    {
        IDoubleArray res = doublesNew.array(correlation.size());
        double shift =(sum/sumOfWeights)*(sum/sumOfWeights);
        for (int i=0; i<res.size(); i++)
            res.set(i, (correlation.get(i)/weights.get(i)) - shift);
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
        int maxtime = parser.getInt("maxtime", 0);
        boolean subtractMean = parser.hasCommand("subtractmean");


        IDoubleList lagtimes = doublesNew.listFrom(0,1);
        while (lagtimes.get(lagtimes.size() - 1) < maxtime)
        {
            lagtimes.append((int)((lagtimes.get(lagtimes.size() - 1) + 1) * 1.1));
        }
        //IDoubleArray lagtimes = doublesNew.arrayFrom(1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000);
        IDoubleArray averageWidths = doublesNew.array(lagtimes.size());
        for (int i = 0; i < averageWidths.size(); i++)
        {
            averageWidths.set(i, 0.1 * lagtimes.get(i));
            //System.out.println(lagtimes.get(i)+"\t"+averageWidths.get(i));
        }
        //System.out.println();

        SmoothedCorrelator correlator = new SmoothedCorrelator(lagtimes, averageWidths);


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
            corr=correlator.getCorrelationMeanFree();
        else
            corr=correlator.getCorrelation();


        //IDoubleArray corr = correlator.correlate(time, E);

        for (int i = 0; i < lagtimes.size(); i++)
        {
            System.out.println(lagtimes.get(i) + "\t" + corr.get(i));
        }
    }
}
