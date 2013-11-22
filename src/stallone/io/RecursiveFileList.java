/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import stallone.api.io.IO;

/**
 *
 * @author noe
 */
public class RecursiveFileList
{
    private File startingDirectory;
    private List<File> allFiles;
    private String filter;
    private List<File> selectedFiles;

    public RecursiveFileList(String startingDirectory)
            throws FileNotFoundException
    {
        this(new File(startingDirectory));
    }

    public RecursiveFileList(String startingDirectory, String _filter)
            throws FileNotFoundException
    {
        this(new File(startingDirectory),_filter);
    }

    public RecursiveFileList(File _startingDirectory)
            throws FileNotFoundException
    {
        this(_startingDirectory,null);
    }

    public RecursiveFileList(File _startingDirectory, String _filter)
            throws FileNotFoundException
    {
        startingDirectory = _startingDirectory;
        validateDirectory(startingDirectory);
        allFiles = listFilesRecursiveUnsorted(startingDirectory);
        Collections.sort(allFiles);

        this.filter = _filter;
        employFilter(_filter);
    }

    // PRIVATE //
    private List<File> listFilesRecursiveUnsorted(File startingDirectory)
            throws FileNotFoundException
    {
        List<File> result = new ArrayList<File>();
        File[] filesAndDirs = startingDirectory.listFiles();
        List<File> filesDirs = Arrays.asList(filesAndDirs);
        for (File file : filesDirs)
        {
            result.add(file); //always add, even if directory
            if (!file.isFile())
            {
                //must be a directory
                //recursive call!
                List<File> deeperList = listFilesRecursiveUnsorted(file);
                result.addAll(deeperList);
            }
        }
        return result;
    }

    private void employFilter(String filter)
    {
        if (filter == null)
            selectedFiles = allFiles;
        else
        {
            selectedFiles = new ArrayList<File>();

            WildcardFilter wcfilter = new WildcardFilter(filter);

            for (File f:allFiles)
            {
                if (wcfilter.accept(f))
                    selectedFiles.add(f);
            }
        }

    }


    /**
     * Directory is valid if it exists, does not represent a file, and can be read.
     */
    private void validateDirectory(File aDirectory)
            throws FileNotFoundException
    {
        if (aDirectory == null)
        {
            throw new IllegalArgumentException("Directory should not be null.");
        }
        if (!aDirectory.exists())
        {
            throw new FileNotFoundException("Directory does not exist: " + aDirectory);
        }
        if (!aDirectory.isDirectory())
        {
            throw new IllegalArgumentException("Is not a directory: " + aDirectory);
        }
        if (!aDirectory.canRead())
        {
            throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
        }
    }

    public int numberOfFiles()
    {
        return(selectedFiles.size());
    }

    public List<File> getFiles()
    {
        return(selectedFiles);
    }

    public List<String> getAbsolutePaths()
    {
        return(IO.util.toStrings(selectedFiles));
    }

    public List<String> getLocalPaths()
    {
        List<String> localpaths = new ArrayList<String>();

        String startingDirectoryPath = startingDirectory.getAbsolutePath();
        for (File f : selectedFiles)
        {
            String lp = f.getAbsolutePath();
            if (lp.startsWith(startingDirectoryPath))
            {
                lp = lp.substring(startingDirectoryPath.length());
                while(lp.startsWith("/"))
                    lp = lp.substring(1);
            }
            else
            {
                throw(new RuntimeException("Should not be here. Inconsistency in file names"));
            }
            localpaths.add(lp);
        }

        return(localpaths);
    }

    public List<String> getLocalSubDirectoryPaths()
    {
        HashSet<String> subdirs  = new HashSet<String>();

        List<String> localpaths = getLocalPaths();
        for (String s:localpaths)
        {
            if (s.contains("/"))
            {
                subdirs.add(s.substring(0,s.lastIndexOf("/")));
            }
        }

        List<String> res = new ArrayList<String>();
        res.addAll(subdirs);
        return(res);
    }

}
