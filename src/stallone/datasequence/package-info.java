/**
 * Datatypes API package for dealing with sequences of numerical data.
 * 
 * This package deals with trajectories, or sequences of numerical data.
 * A DataSequence is an ordered sequence of N IDoubleArrays, each with a 
 * specific fixed format (e.g. D = n x d matrices, or length D = d arrays).
 * N is the length of a sequence, D is its dimension.
 * Often we have sequences that are too large to be stored in main memory.
 * Thus the data containers in this package permit data streaming by iterating
 * over IDoubleArrays in a file. However, fully loading a sequence is also 
 * permitted when the memory allows.
 * This package also defines the API for reading and writing from/to collections
 * of DataSequences, i.e. databases or directories of trajectories.
 * 
 * TODO: Should we rename DataSequence -> Trajectory?
 */
package stallone.datasequence;