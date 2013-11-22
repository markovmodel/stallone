/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.util;

/**
   Reads an Argument list in the form
   -command [arg1 [arg2 .. ]] -command [arg1 [arg2 .. ]] ...
   <br>
   Please note that each command must start with a dash "-". Dashed are
   therefore not allowed at the beginning of arguments. If a "-" sign is followed by
   a number, that is treated as a negative number, otherwise it is always treated as
   a command.
   <br>
   Each command ends with a whitespace, its arguments are separated by
   whitespaces and its argument list runs to the next word starting with "-"
   Arguments that contain spaces must be protected by quotation marks (").
   <br>
   Each argument is attached to its preceding command. Arguments without
   commands (as in many linux programs, e.g. convert or lpr) are currently
   not supported
 */
public class Arguments
{
    private String[] commands;
    private String[][] arguments;

    private Arguments(int nCommands)
    {
	commands = new String[nCommands];
	arguments = new String[nCommands][];
    }

    public Arguments(String args)
	{this(StringTools.split(args));}

    public Arguments(String[] args)
    {
	int nCommands = 0;
	for (int i=0; i<args.length; i++)
	    if (isCommand(args[i]))
		nCommands++;

	commands = new String[nCommands];
	arguments = new String[nCommands][0];
	int k=-1;
	for (int i=0; i<args.length; i++)
	    {
		if (isCommand(args[i]))
		    {
			commands[++k] = args[i].substring(1,args[i].length());
			continue;
		    }

		if (k == -1)
		    throw(new IllegalArgumentException("Argument list must start with a command, preceded by '-'"));
		arguments[k] = StringTools.concat(arguments[k], args[i]);
	    }
    }

    /*public static Arguments fromFile(String file)
    {
	ASCIIReader reader = new ASCIIReader(file);
	int nCommands = (int)reader.getNLines();
	Arguments res = new Arguments(nCommands);
	for (int i=0; i<nCommands; i++)
	    {
		String[] words = reader.readWords();
		res.commands[i] = words[0];
		res.arguments[i] = StringTools.subarray(words, 1, words.length);
	    }
	return(res);
    }*/

    private boolean isCommand(String a)
    {
	if (!(a.startsWith("-")))
	    return(false);
	if (StringTools.isDouble(a) || StringTools.isInt(a) || StringTools.isDoubleArray(a))
	    return(false);
	else
	    return(true);
    }

    /**
       @return true iff the specified command is given
    */
    public boolean hasCommand(String cmd)
    {
	return(StringTools.contains(commands, cmd));
    }

    public String[] getCommands()
    {
        return commands;
    }

    public String[] getArguments(String cmd)
    {
	int i = StringTools.findForward(commands, cmd);
	if (i<0)
	    return(null);
	else
	    return(arguments[i]);
    }

    /**
       @return true iff the specified command is given
    */
    public boolean hasArgument(String cmd, String arg)
    {
	if (!hasCommand(cmd))
	    return(false);
	return(StringTools.contains(getArguments(cmd), cmd));
    }

    public int getNArguments(String cmd)
    {   return(getArguments(cmd).length);}

    /**
       returns Argument with selected index
     */
    public String getArgument(String cmd, int iArg)
    {	return(getArguments(cmd)[iArg]); }

    public int getIntArgument(String cmd, int iArg)
    {   return(StringTools.toInt(getArgument(cmd, iArg)));}

    public int[] getIntArrayArgument(String cmd, int iArg)
    {   return(StringTools.toIntArray(getArgument(cmd, iArg)).getArray());}

    public double getDoubleArgument(String cmd, int iArg)
    {   return(StringTools.toDouble(getArgument(cmd, iArg)));}

    public double[] getDoubleArrayArgument(String cmd, int iArg)
    {   return(StringTools.toDoubleArray(getArgument(cmd, iArg)));}

    /**
       returns first Argument to given cmd
     */
    public String getArgument(String cmd)
    {	return(getArguments(cmd)[0]); }

    public int getIntArgument(String cmd)
    {   return(getIntArgument(cmd, 0));}

    public int[] getIntArrayArgument(String cmd)
    {   return(getIntArrayArgument(cmd, 0));}

    public double getDoubleArgument(String cmd)
    {   return(getDoubleArgument(cmd, 0));}

    public static void main(String[] args)
    {
	new Arguments(args);
    }

}
