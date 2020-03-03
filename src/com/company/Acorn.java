package com.company;

public class Acorn implements Grow {
    private double Height;
    Acorn(){
        this.Height = -1.0;
    }
    @Override
    public void grow() {
        this.Height += 0.5;
    }
    public double getHeight(){
        return this.Height;
    }
    public int isGrow(){
        if (this.Height <= 0){
            return -1;
        }
        else{
            if (this.Height <5){
                return 1;
            }
            else{
                return 2;
            }
        }
    }
}
