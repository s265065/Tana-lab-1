package com.company;

public class Creature {
    String name;
    Creature(String name){
        this.name=name;
    }
    @Override
    public String toString(){
        return name;
    }
    @Override
    public int hashCode(){
        return name.hashCode();
    }
}

