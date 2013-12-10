package moviedemo

import org.jblas.DoubleMatrix
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import util.Random
import scala.collection.immutable.SortedMap
import scala.util._
import java.io._
import scala.io.Source

object MovieRec {

  def main(args: Array[String]){

    // sbt/sbt "run-main distopt.SVML1BatchSGD --train=data/astro-ph_29882.dat --test=data/astro-ph_test.dat --nfeats=99757 --lambda=0.00005 --niters=200 --batchfrac=0.03125"

    val options =  args.map { arg =>
      arg.dropWhile(_ == '-').split('=') match {
        case Array(opt, v) => (opt -> v)
        case Array(opt) => (opt -> "true")
        case _ => throw new IllegalArgumentException("Invalid argument: " + arg)
      }
    }.toMap

    // read in input, defaults
    val master = options.getOrElse("master", "local[4]")
    val trainfile = options.getOrElse("train", "")
    val testfile = options.getOrElse("test", "")
    val lambda = options.getOrElse("lambda", "0.01").toDouble
    val niters = options.getOrElse("niters", "200").toInt
    val jar = options.getOrElse("jar", "")
    val sparkhome = options.getOrElse("sparkhome", System.getenv("SPARK_HOME"))
    val nfeats = options.getOrElse("nfeats", "100").toInt
    val batchfrac = options.getOrElse("batchfrac", "0.1").toDouble
    val outfile = options.getOrElse("outfile", "")
    val nsplits = options.getOrElse("nsplits","1").toInt
    val imgnet = options.getOrElse("imgnet","false").toBoolean

    // print out input
    println("master:       " + master);     println("train:        " + trainfile)
    println("test:         " + testfile);   println("lambda:       " + lambda)
    println("niters:       " + niters);     println("jar:          " + jar)
    println("sparkhome:    " + sparkhome);  println("nfeats:       " + nfeats)
    println("batchfrac:    " + batchfrac);  println("outputfile:   " + outfile)
    println("nsplits:      " + nsplits);    println("imagenet:     " + imgnet)

    // start spark context
    val sc = new SparkContext(master, "SVML1BatchSGD",sparkhome,List(jar));

    // var data: org.apache.spark.rdd.RDD[SparseClassificationPoint] = null
    // var testData: org.apache.spark.rdd.RDD[SparseClassificationPoint] = null
    // // read in train file
    // if(imgnet){
    //   val allData = OptUtils.loadImageNetData(sc,trainfile,nsplits).map( pt => (Math.random < 0.8,pt))
    //   data = allData.filter{ case(a,b) => a==true }.map{ case(a,b) => b }
    //   testData = allData.filter{ case(a,b) => a==false }.map{ case(a,b) => b }
    //   println("training data size: " + data.count())
    //   println("test data size: " + testData.count())
    // }
    // else{
    //   data = OptUtils.loadLIBSVMData(sc,trainfile,nsplits);
    //   testData = OptUtils.loadLIBSVMData(sc,testfile,nsplits);
    // }

    // // print info about batch size
    // val parts = data.partitions.size
    // println("number of partitions: " + parts)
    // val sampSize = Math.floor(batchfrac * data.count())
    // println("sample size:          " + sampSize)

    // // run SGD
    // val (objVals, testErrs, cmCounter, cpCounter, times) 
    //   = runSVML1BatchSGD(sc,data,testData,nfeats,lambda,niters,batchfrac)

    // // write data to file
    // OptUtils.writeData(outfile,"BatchSGD",trainfile,testfile,lambda,niters,batchfrac,
    //     parts,objVals,testErrs,cmCounter,cpCounter,times)

    sc.stop()

  }

  // def runSVML1BatchSGD(sc: SparkContext, data: RDD[SparseClassificationPoint], testData: RDD[SparseClassificationPoint],
  //   nfeats:Int, lambda: Double, niters: Int, batchfrac: Double) 
  //   : (Array[Double], Array[Double], Array[Double], Array[Double], Array[Double]) = {

  //   val outsize = 200
  //   // idea: want to capture no more than 200 points
  //   var rec = 1
  //   if(niters>999){
  //     rec = Math.floor(niters/outsize).toInt
  //   }

  //   // initialize variables
  //   val n = data.count()
  //   val testn = testData.count()
  //   val parts = data.partitions.size
  //   var elapsedTime = 0.0
  //   var cmcnt = 0.0
  //   var cpcnt = 0.0
  //   var w = Array.fill(nfeats)(0.0)

  //   // intialize output
  //   val objVals = Array.fill(outsize+1)(0.0)
  //   val testErrs = Array.fill(outsize+1)(0.0)
  //   val cmCounter = Array.fill(outsize+1)(0.0)
  //   val cpCounter = Array.fill(outsize+1)(0.0)
  //   val times = Array.fill(outsize+1)(0.0)

  //   // compute initial errors
  //   objVals(0) = OptUtils.computeObjective(data,w,n,lambda)
  //   testErrs(0) = OptUtils.computeAvgLoss(testData,w,testn)

  //   for(t <- 1 until niters+1){

  //     // Time Implementation
  //     val tstart = System.currentTimeMillis

  //     /** Batch GD UPDATE **/

  //     // update step size
  //     val step = 1/(lambda*(t+2))

  //     // cpcnt = cpcnt + 3

  //     // scale weight vector
  //     val scale = 1-step*lambda
  //     w = w.map(_*scale)

  //     // cpcnt = cpcnt + 2 + nfeats

  //     // compute gradient from minibatch
  //     val minibatch = data.sample(false,batchfrac,System.currentTimeMillis.toInt)
  //     val batchSize = minibatch.count()
  //     val gradientSum = minibatch.map{ point => OptUtils.computeHingeGradient(point,w) }.reduce(_ plus _)

  //     //cmcnt = cmcnt + batchSize.toDouble*nfeats*64 // sending w to each machine
  //     //cmcnt = cmcnt + batchSize.toDouble*2*nfeats*64 // reducing the gradients
  //     //cpcnt = cpcnt + batchSize.toDouble*(2*nfeats+1)

  //     // update weight vector
  //     val update = gradientSum.times(step/batchSize)
  //     w = update.plus(w)

  //     //cpcnt = cpcnt + 2*nfeats

  //     // [optional] project
  //     // val wnorm = OptUtils.normDense(w)
  //     //cpcnt  = cpcnt + nfeats

  //     // if(wnorm > 1/lambda){
  //     //   val proj = 1/(Math.sqrt(lambda*wnorm))
  //     //   w = w.map(_*proj)
  //     //   cpcnt = cpcnt + 3 + nfeats
  //     // }

  //     elapsedTime = elapsedTime + (System.currentTimeMillis-tstart)


  //     /** End Update **/

  //     if(t % rec == 0){

  //       println("Iteration: " + t)

  //       val idx = Math.floor(t/rec).toInt 
  //       // compute objective function evaluation
  //       objVals(idx) = OptUtils.computeObjective(data,w,n,lambda)
  //       // compute test error
  //       testErrs(idx) = OptUtils.computeAvgLoss(testData,w,testn)
  //       // track communication
  //       cmCounter(idx) = 64*(data.partitions.size*nfeats + batchSize.toDouble*nfeats)//cmcnt
  //       // track computation
  //       cpCounter(idx) = 5+nfeats+2*batchSize.toDouble*(nfeats+1)//cpcnt
  //       // compute elapsed time
  //       times(idx) = elapsedTime
  //     }
  //   }

  //   println("final objective value: " + objVals(outsize))
  //   println("final test error:  " + testErrs(outsize))

  //   return (objVals, testErrs, cmCounter, cpCounter, times)
  // }

}
