/**
 * Algorithms API package for numerical data processing in pipelines or networks.
 * 
 * Each data processing step or stage is a DataProcessor. DataProcessors can
 * read, transform, use or write data. DataProcessors can be connected so as to
 * define the data processing logic and dependencies. From this, the sequence 
 * in which computations are made is defined.
 * A data processor can work with a collection of DataSequences.
 * The data this algorithms package works with are stored in DataSequences,
 * i.e. sequences of IDoubleArrays. 
 */
package stallone.dataprocessing;