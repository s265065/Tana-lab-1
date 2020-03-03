package com.company;

import java.time.ZonedDateTime;

public class Bear extends Creature implements SpeakthenThink {
    final String type;
    public ZonedDateTime dataCreate;
    Bear(String name, String type){
        super(name);
        this.type = type;
        this.dataCreate = ZonedDateTime.now();
    }
    public String getName(){
        return this.name;
    }
    public String getType(){
        return this.type;
    }
    @Override
    public void speak(){
        System.out.println("неважно, чем он занят, так как он толстеть не станет, а ведь он толстеть не станет");
    }
}
