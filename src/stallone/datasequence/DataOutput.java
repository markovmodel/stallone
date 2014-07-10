/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import static stallone.api.API.*;
import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import stallone.api.API;
import stallone.api.dataprocessing.IDataProcessor;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataOutput;
import stallone.api.datasequence.IDataWriter;


/**
 *
 * Class that writes sequences of numerical data (DataSequences of IDoubleArrays)
 * to a set of files in one target directory.
 * 
 * TODO: Rename to DataWriter, TrajectoryWriter or TrajDBWriter?
 * 
 * @author noe
 */
public class DataOutput implements IDataOutput
{
    private String outputPath;
    private IDataWriter writer;
    private IDataInput input;

    // naming convention
    private int namingConvention = 0;
    private final int NAMING_KEEP = 0, NAMING_CHANGEEXT = 1, NAMING_NEW = 2;
    private String newExtension = null;
    private String prefix = "", suffix = ".dat";
    
    /**
     * 
     */
    public DataOutput(String _path, IDataWriter _writer)
    {
        this.outputPath = _path;
        this.writer = _writer;
    }

    /**
     * 
     */
    public DataOutput(String _path, IDataWriter _writer, IDataProcessor _input)
    {
        this.outputPath = _path;
        this.writer = _writer;
        addSender(_input);
    }
    
    
    /**
     * Set naming convention conservative, i.e. all original filename are kept 
     * but are written to the target path.
     */
    public void namingConventionKeep()
    {
        this.namingConvention = this.NAMING_KEEP;
    }

    
    /**
     * Set naming convention conservative, i.e. all original filename are kept 
     * but are written to the target path.
     */
    public void namingConventionChangeExtension(String ext)
    {
        this.namingConvention = this.NAMING_CHANGEEXT;
        this.newExtension = ext;
    }
    
    
    /**
     * Set naming convention to new, i.e. all filename will be set to
     * [prefix][N][suffix], where N is the index of the trajectory.
     * If you want dots in the filename, they have to be included in prefix or
     * suffix.
     */
    public void namingConventionNew(String _prefix, String _suffix)
    {
        this.namingConvention = this.NAMING_NEW;
        this.prefix = _prefix;
        this.suffix = _suffix;
    }
    
    
    private String getOutputName(int index)
    {        
        if (this.namingConvention == this.NAMING_KEEP)
        {
            String filename = io.getFilename(input.name(index));
            return this.outputPath+"/"+filename;
        }
        else if (this.namingConvention == this.NAMING_CHANGEEXT)
        {
            String filename = io.getBasename(input.name(index));
            return this.outputPath+"/"+filename+"."+newExtension;
        }
        else if (this.namingConvention == this.NAMING_NEW)
        {
            return this.outputPath+"/"+prefix+index+suffix;
        }
        
        throw new RuntimeException("Unknown naming convention set.");
    }
    
    
    //==========================================================================
    //
    // Data processing methods
    //
    //==========================================================================
    
    /**
     * Sets the receiver when called once. 
     * @throws RuntimeException when called twice because PCA can only have one input.
     * @param receiver
     */
    @Override
    public void addSender(IDataProcessor sender)
    {
        if (this.input != null)
            throw new RuntimeException("Trying to add a second sencer to PCA. This is not possible.");
        
        if (sender instanceof IDataInput)
            this.input = (IDataInput)sender;
        else
            throw new IllegalArgumentException("Illegal input type: sender must be an instance of IDataInput");
    }


    /**
     * Does nothing
     * @param sender 
     */
    @Override
    public void addReceiver(IDataProcessor receiver)
    {
    }

    /**
     * Checks if all writing operations can be executed.
     */
    @Override
    public void init()
    {
        // input available?
        if (this.input == null)
            throw new RuntimeException("Data output cannot be initialized. "
                    + "The input hasn't been set yet.");
        
        // output folder exists?
        File f = new File(outputPath);
        if (!f.exists() || !f.isDirectory()) 
        {
            throw new RuntimeException("Target path "+outputPath+" does not"
                    + "exist or is not a directory");
        }
        
        // check if all files can be written and are uniquely named
        TreeSet<String> set = new TreeSet<String>();
        for (int i=0; i<input.numberOfSequences(); i++)
        {
            String s = getOutputName(i);
            if (!(new File(s).canWrite()))
                throw new RuntimeException("File "+s+" cannot be written.");
            set.add(s);
        }
        if (set.size() < input.numberOfSequences())
            throw new RuntimeException("Aborting write because some output file"
                    + " names appear to be duplicate. This would cause data loss");
    }

    /**
     * Write all trajectories to output
     */
    @Override
    public void run()
    {
        for (int i=0; i<input.numberOfSequences(); i++)
        {
            String path = getOutputName(i);
            int nFrames = input.size(i);
            int nDim = input.dimension();
            try
            {
                // write all
                writer.open(path, nFrames, nDim);
                writer.addAll(input.getSequence(nFrames));
                writer.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Nothing to do.
     */
    @Override
    public void cleanup()
    {
    }
    
    
}
