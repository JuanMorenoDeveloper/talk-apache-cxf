package ve.com.proitcsolution.cxf.controller;

import org.temp.calculator.CalculatorSoap;

public class CalculatorController implements CalculatorSoap {

  @Override
  public int subtract(int intA, int intB) {
    return intA - intB;
  }

  @Override
  public int divide(int intA, int intB) {
    if (intB == 0) {
      throw new IllegalArgumentException("intB can't be zero");
    }
    return intA / intB;
  }

  @Override
  public int add(int intA, int intB) {
    return intA + intB;
  }

  @Override
  public int multiply(int intA, int intB) {
    return intA * intB;
  }
}
