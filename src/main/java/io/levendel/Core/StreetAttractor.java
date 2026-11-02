package io.levendel.Core;

public interface StreetAttractor {

    double evaluate(StreetNode current, StreetNode proposed);

}