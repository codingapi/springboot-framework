package com.codingapi.springboot.framework.math;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Arithmetic {

    private BigDecimal value;

    private final static int DEFAULT_SCALE = 4;
    private final static RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    public static Arithmetic zero(){
        return parse(0);
    }

    public static Arithmetic one(){
        return parse(1);
    }

    public static Arithmetic parse(int value){
        return new Arithmetic(value);
    }

    public static Arithmetic parse(float value){
        return new Arithmetic(value);
    }

    public static Arithmetic parse(double value){
        return new Arithmetic(value);
    }

    public static Arithmetic parse(long value){
        return new Arithmetic(value);
    }

    public static Arithmetic parse(String value){
        return new Arithmetic(value);
    }


    public Arithmetic(String value) {
        this.value = new BigDecimal(value);
    }

    public Arithmetic(int value) {
        this(String.valueOf(value));
    }

    public Arithmetic(float value) {
        this(String.valueOf(value));
    }

    public Arithmetic(double value) {
        this(String.valueOf(value));
    }

    public Arithmetic(long value) {
        this(String.valueOf(value));
    }

    public Arithmetic add(Arithmetic val){
        this.value = this.value.add(val.value);
        return this;
    }


    public Arithmetic add(String val){
        this.value = this.value.add(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic add(int val){
        this.value = this.value.add(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic add(float val){
        this.value = this.value.add(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic add(double val){
        this.value = this.value.add(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic add(long val){
        this.value = this.value.add(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic sub(Arithmetic val){
        this.value =this.value.subtract(val.value);
        return this;
    }

    public Arithmetic sub(String val){
        this.value =this.value.subtract(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic sub(int val){
        this.value =this.value.subtract(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic sub(float val){
        this.value =this.value.subtract(new Arithmetic(val).value);
        return this;
    }


    public Arithmetic sub(double val){
        this.value =this.value.subtract(new Arithmetic(val).value);
        return this;
    }


    public Arithmetic sub(long val){
        this.value =this.value.subtract(new Arithmetic(val).value);
        return this;
    }


    public Arithmetic mul(Arithmetic val){
        this.value = this.value.multiply(val.value);
        return this;
    }

    public Arithmetic mul(String val){
        this.value = this.value.multiply(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic mul(int val){
        this.value = this.value.multiply(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic mul(float val){
        this.value = this.value.multiply(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic mul(double val){
        this.value = this.value.multiply(new Arithmetic(val).value);
        return this;
    }

    public Arithmetic mul(long val){
        this.value = this.value.multiply(new Arithmetic(val).value);
        return this;
    }


    public Arithmetic div(Arithmetic val){
        this.value = this.value.divide(val.value,DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        return this;
    }

    public Arithmetic div(String val){
        this.value = this.value.divide(new Arithmetic(val).value, DEFAULT_SCALE,DEFAULT_ROUNDING_MODE);
        return this;
    }

    public Arithmetic div(int val){
        this.value = this.value.divide(new Arithmetic(val).value,DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        return this;
    }


    public Arithmetic div(float val){
        this.value = this.value.divide(new Arithmetic(val).value,DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        return this;
    }

    public Arithmetic div(double val){
        this.value = this.value.divide(new Arithmetic(val).value,DEFAULT_SCALE,DEFAULT_ROUNDING_MODE);
        return this;
    }

    public Arithmetic div(long val){
        this.value = this.value.divide(new Arithmetic(val).value, DEFAULT_SCALE,DEFAULT_ROUNDING_MODE);
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getStringValue() {
        return value.toString();
    }

    public BigInteger getBigIntegerValue() {
        return value.toBigInteger();
    }

    public int getIntValue() {
        return value.intValue();
    }

    public double getDoubleValue() {
        return value.doubleValue();
    }

    public float getFloatValue() {
        return value.floatValue();
    }

    public long getLongValue() {
        return value.longValue();
    }

    public Arithmetic halfUpScale2(){
        return halfUpScale(2);
    }

    public Arithmetic halfUpScale(int scale){
        value = value.setScale(scale,RoundingMode.HALF_UP);
        return this;
    }

}

