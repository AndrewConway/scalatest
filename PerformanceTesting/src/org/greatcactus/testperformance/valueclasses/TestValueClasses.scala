/**
 * Copyright 2013 Andrew Conway. All rights reserved
 */
package org.greatcactus.testperformance.valueclasses


/**
 * Test the performance overhead of value classes pre/post 2.10
 */

object TestValueClasses {
  def test(name:String,work : () => Unit) {
    work()  // make sure classes are loaded.
    System.gc()
    val startTime = System.nanoTime
    work()
    val endTime = System.nanoTime
    val timePerIteration = (endTime-startTime).doubleValue/1000000.0
    println("%s\t%.3fms".format(name,timePerIteration))
  }
  
  val randomList : List[Int] = Array.fill(10000){(Math.random()*1000000).toInt}.toList

  def sortListSimple {
    for (dummy<-0 until repetitions) randomList.sorted
  }
  def sortListSimple1 {
    for (dummy<-0 until repetitions) randomList.sorted(ExplicitIntOrdering)
  }

  def sortListExplicit {
    for (dummy<-0 until repetitions) randomList.sortBy{_.toInt}
  }
  
  def sortArraySimple {
    val a : Array[Int] = randomList.toArray
    for (dummy<-0 until repetitions) a.sorted
  }

  def sortArraySimple1 {
    val a : Array[Int] = randomList.toArray
    for (dummy<-0 until repetitions) a.sorted(ExplicitIntOrdering)
  }

  def sortArrayExplicit {
    val a : Array[Int] = randomList.toArray
    for (dummy<-0 until repetitions) a.sortBy{_.toInt}
  }
  
  val size = 100000
  val repetitions = 1000
  def workPlain() {
    val values = new Array[Double](size)
    val inds = (0 until size).toArray
    val magic = 87
    for (dummy<-0 until repetitions) {
      for (i<-0 until size) {
        val ind = inds(i)
        values(ind)+=1.0 // (if (ind==magic) 1.0 else 2.0)
      }
    }
  }
  
  def workIndex() {
    val values = new Array[Double](size)
    val inds = (0 until size).toArray.map{CompiledVariableIndex(_)}
    val magic = CompiledVariableIndex(87)
    for (dummy<-0 until repetitions) {
      for (i<-0 until size) {
        val ind = inds(i)
        values(ind.index)+=1.0 // (if (ind==magic) 1.0 else 2.0)
      }
    }
  }
  
  def workFull() {
    val values = new CompiledVariableData(new Array[Double](size))
    val inds = (0 until size).toArray.map{CompiledVariableIndex(_)}
    val magic = CompiledVariableIndex(87)
    for (dummy<-0 until repetitions) {
      for (i<-0 until size) {
        val ind = inds(i)
        values(ind)+=1.0 // (if (ind==magic) 1.0 else 2.0)
      }
    }
  }
  
  def main(args:Array[String]) {
    val size = 10000000
    println(Runtime.getRuntime.maxMemory/1024.0/1024.0)
    test("List Simple",sortListSimple _)
    test("List ExplicitIntOrdering",sortListSimple1 _)
    test("List Explicit",sortListExplicit _)
    test("Array Simple",sortArraySimple _)
    test("Array ExplicitIntOrdering",sortArraySimple1 _)
    test("Array Explicit",sortArrayExplicit _)
    test("Plain",workPlain _)
    test("Index",workIndex _)
    test("Full",workFull _)
  }
}

object ExplicitIntOrdering extends Ordering[Int] {
  def compare(x: Int, y: Int) =
      if (x < y) -1
      else if (x == y) 0
      else 1
}

/** Compiled variables are represented as integer offsets into an array. For type safety it can be useful to wrap them in this class to stop them being confused with other integers. */
class CompiledVariableIndex(val index:Int) extends AnyVal { // TODO extends AnyVal in 2.10 and get rid of equals and hashcode.
 // override def toString = index.toString
}

object CompiledVariableIndex {
  def apply(index:Int) = new CompiledVariableIndex(index)
} 

/** The value of Compiled variables are represented as an array of doubles, indexed by CompiledVariableIndex. For type safety it can be useful to wrap them in this class to stop them being confused with other integers. */
class CompiledVariableData(val data:Array[Double]) extends AnyVal { // TODO make a value class in 2.10
  def apply(index:CompiledVariableIndex) = data(index.index)
  def update(index:CompiledVariableIndex,value:Double) { data(index.index)=value }
}

