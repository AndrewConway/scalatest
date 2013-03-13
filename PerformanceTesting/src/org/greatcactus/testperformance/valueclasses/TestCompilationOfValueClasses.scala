package org.greatcactus.testperformance.valueclasses

class TestCompilationOfValueClasses {

  val vInt = 3
  val vCV = new CompiledVariableIndex(3)
  
  val listInt : List[Int] = Nil
  val listCV : List[CompiledVariableIndex] = Nil
  val listString : List[String] = Nil
  
  val hLI = listInt.head
  val hCV = listCV.head
  val hS = listString.head
  
  def fInt(x:Int) = x
  def fCV(x:CompiledVariableIndex) = x
}