package com.example.hciwearos;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class processing extends PApplet {

    //FittsTask t;
    CalloutTask t;

    PGraphics calloutImage;
    PGraphics calloutMask;

    public void setup(){


        //CalloutImage Setting
        calloutImage = createGraphics(320,320);

        background(125,125,125);
        //t = new FittsTask();
        t = new CalloutTask();
        t.drawApplication();
        t.setTouchPos(0,0);



    }

    public void draw(){
        background(125,125,125);
        t.startTask();
        //deliver current finger position
        t.setTouchPos(mouseX,mouseY);
    }

    //start of task
    public void mousePressed(){
        t.touch = true;
    }

    //end of task
    public void mouseReleased(){
        t.touch = false;
        t.onTask = false;
        t.taskDiscriminator();
    }
    //manual reset task
    public void keyPressed(){
        if(key=='r'){
            t.resetTask();
        }
    }
    class Application{

        boolean target = false;
        boolean onFinger = false;
        int x;
        int y;
        int size = 30;

        Application(int x, int y){
            this.x = x;
            this.y = y;
        }

        public void setTarget(){
            this.target = true;
        }

        public void deleteTarget(){
            this.target=false;
        }

        public int getSize(){
            return this.size;
        }

        public void setOnFinger(boolean state){
            onFinger = state;
        }

        public PVector getPosition(){
            return new PVector(this.x,this.y);
        }

        public void update(){
            //if application is target fill red
            fill(255,255,255);
            if(target){
                if(onFinger){
                    fill(0,255,0);
                }
                else{
                    fill(255,0,0);
                }
            }
            ellipse(x,y,size,size);
            fill(255,255,255);
        }

        public void calloutUpdate(){
            //println("hlelo");

            //if application is target fill red
            calloutImage.fill(255,255,255);
            if(target){
                if(onFinger){
                    calloutImage.fill(0,255,0);
                }
                else{
                    calloutImage.fill(255,0,0);
                }
            }
            calloutImage.ellipse(x,y,size,size);
            calloutImage.fill(255,255,255);

        }


    };
    class CalloutTask{
        ArrayList<Application> appList = new  ArrayList<Application>();
        PImage callout;
        PImage magnified, normal;
        int targetIndex =-1;
        boolean touch = false;
        boolean onTask = false;
        boolean onTouch = false;
        boolean first = true;
        float mouseX, mouseY;

        //FOR ZOOM
        float scaleFactor = 1;
        float speedFactor = 0.03f;
        float translateX, translateY;
        //FOR CALLOUT
        int calloutSize = 90;
        int calloutOffsetX = 0;
        int calloutOffsetY = 0;
        int calloutOffset=80;

        int taskMode = 2;


        CalloutTask(){
            //init fitts task
            initApplication();
            setTarget();
            setImage();


        }

        public void initApplication(){
            //fill application on window
            for (int i = 40; i < width ; i=i+40){
                for(int j = 40; j < height ; j=j+40){
                    appList.add(new Application(i,j));
                }
            }
        }

        public void setTarget(){
            targetIndex = PApplet.parseInt(random(0,appList.size()));
            appList.get(targetIndex).setTarget();
        }

        public void setImage(){
            // translate(width/2,height/2)
            drawApplication();
            normal=get();
            // scale(2);
            drawApplication();
            magnified=get();


        }

        public void drawApplication(){

            if(taskMode==0){

                calloutImage.beginDraw();
                calloutImage.pushMatrix();
                pushMatrix();
                translate(translateX, translateY);
                calloutImage.translate(translateX,translateY);

                //set position of callout
                setCalloutPosition();
                //check mouse on target
                checkFingerOnTarget();

                scale(scaleFactor);
                calloutImage.scale(scaleFactor);
                background(255);

                calloutImage.background(255);
                for(Application a : appList){
                    a.update();
                    a.calloutUpdate();
                }

                popMatrix();
                calloutImage.popMatrix();
                calloutImage.endDraw();
            }
            else{
                calloutImage.beginDraw();
                background(255);
                calloutImage.background(255);
                for(Application a : appList){
                    a.update();
                    a.calloutUpdate();
                }
                calloutImage.endDraw();
            }


        }

        public void setCalloutPosition(){

            if(mouseY-calloutOffset-calloutSize/2>0){
                calloutOffsetX=0;
                calloutOffsetY= -1 * calloutOffset;
            }
            else if(mouseX-calloutOffset-calloutSize/2>0){
                calloutOffsetX=-1 * calloutOffset;
                calloutOffsetY=0;
            }
            else{
                calloutOffsetX=calloutOffset;
                calloutOffsetY=0;
            }

        }

        public void checkFingerOnTarget(){
            if(getTargetDistance()<15){
                appList.get(targetIndex).setOnFinger(true);
            }
            else{
                appList.get(targetIndex).setOnFinger(false);
            }
        }

        public void startTask(){
            //zoom with callout
            if(taskMode==0){
                if(touch){
                    zoom();
                }

                fill(255);
                rect(0, 0, width, height);
                calloutMask = createGraphics(320,320); //size of mask

                drawApplication();

                calloutMask.beginDraw();
                calloutMask.ellipse(mouseX, mouseY, calloutSize,calloutSize);
                calloutMask.endDraw();
                calloutImage.mask(calloutMask);
                noFill();
                strokeWeight(2);
                ellipse(mouseX+calloutOffsetX, mouseY+calloutOffsetY, calloutSize,calloutSize);

                fill(255,255,0);
                strokeWeight(1);
                image(calloutImage, calloutOffsetX, calloutOffsetY);
                ellipse(mouseX+calloutOffsetX, mouseY+calloutOffsetY, 2,2);

            }
            //callout
            else if(taskMode==1){
                //set position of callout
                setCalloutPosition();

                //check mouse on target
                checkFingerOnTarget();

                fill(255);
                rect(0, 0, width, height);
                calloutMask = createGraphics(320,320); //size of mask

                t.drawApplication();
                setCalloutPosition();

                calloutMask.beginDraw();
                calloutMask.ellipse(mouseX, mouseY, calloutSize,calloutSize);
                calloutMask.endDraw();
                calloutImage.mask(calloutMask);
                noFill();
                strokeWeight(2);
                ellipse(mouseX+calloutOffsetX, mouseY+calloutOffsetY, calloutSize,calloutSize);

                fill(255,255,0);
                strokeWeight(1);
                image(calloutImage, calloutOffsetX, calloutOffsetY);
                ellipse(mouseX+calloutOffsetX, mouseY+calloutOffsetY, 2,2);
            }
            //zoom
            else if(taskMode==2){
                if(touch){
                    zoom();
                }


                checkFingerOnTarget();
                pushMatrix();
                translate(translateX, translateY);
                scale(scaleFactor);
                drawApplication();
                popMatrix();
            }
        }

        public void setTouchPos(float mouseX, float mouseY){
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public void taskDiscriminator(){
            //println(getTargetDistance());

            //task Success


            if(getTargetDistance() < 15){
                //mark non error on jsonObject
                println("success");
                resetTask();
            }
            else{
                println("fail");
                if(taskMode==2 || taskMode==0){
                    this.scaleFactor=1;
                    this.translateX = 0;
                    this.translateY = 0;
                }
            }


        }

        //reset task
        public void resetTask(){



            println(targetIndex);
            this.deleteTarget();
            this.setTarget();
            println(targetIndex);
            println("*****");

            if(taskMode==2 || taskMode==0){
                this.scaleFactor=1;
                this.translateX = 0;
                this.translateY = 0;
            }
            taskMode = PApplet.parseInt(random(0, 3));
            first = true;
            onTask = false;
        }

        public void deleteTarget(){
            appList.get(targetIndex).deleteTarget();
            targetIndex = -1;
        }

        public float getTargetDistance(){
            //get targetPosition
            PVector  targetPosVector;
            PVector  fingerPosVector;
            if(taskMode==1){
                targetPosVector = appList.get(targetIndex).getPosition();
                //calculate fingerPosition based on current camera
                fingerPosVector = new PVector(mouseX,mouseY);
            }
            else{

                targetPosVector = appList.get(targetIndex).getPosition();
                fingerPosVector = new PVector((this.mouseX-translateX)/scaleFactor, (this.mouseY-translateY)/scaleFactor);
            }

            //update New distance
            return PVector.dist(targetPosVector, fingerPosVector);
        }

        public void zoom(){

            if(this.touch){
                setScaleFactor( getScaleFactor()+ this.speedFactor);

                setTranslatePos(getTouchPosX() -mouseX * this.speedFactor,
                        getTouchPosY() -mouseY* this.speedFactor);
                //this.count--;
            }

        }
        public float getScaleFactor(){
            return this.scaleFactor;
        }

        public void setScaleFactor(float sFactor){
            this.scaleFactor = sFactor;
        }
        //set and get function for Translate coordinate
        public void setTranslatePos(float translateX, float translateY){
            this.translateX = translateX;
            this.translateY = translateY;
        }

        public float getTouchPosX(){
            return this.translateX;
        }
        public float getTouchPosY(){
            return this.translateY;
        }


    };

//import android.os.Environment;

    class FittsTask{
        //Application List
        ArrayList<Application> appList = new  ArrayList<Application>();

        //For temporarily record distance and width based on time for one trial
        JSONArray distanceArray = new JSONArray();
        JSONArray widthArray = new JSONArray();
        int arrayIndexCounter = 0;

        //recorded data
        long startTime=0;
        long endTime=0;
        boolean errorTrial = false;
        int taskTrial = 0;
        float targetDistance = 0;

        //Camera variable
        float translateX, translateY;
        float prevMouseX, prevMouxeY;
        float mouseX, mouseY;

        //setting for fitts rule
        float scaleFactor = 1;
        float speedFactor = 0.02f;
        long lastTouchTime = 0;
        int targetIndex =-1;
        int count=0;
        long lastStampTime = 0;
        boolean touch = false;
        boolean onTask = false;
        boolean onTouch = false;
        boolean first = true;
        //Export file fittsResult is consist of multiple fittsTrialResult
        JSONArray fittsResult;
        JSONObject fiitsTrialResult;

        FittsTask(){
            //init fitts task
            initApplication();
            setTarget();
        }

        //set current finger position
        public void setTouchPos(float mouseX, float mouseY){
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }


        //set and get function for Translate coordinate
        public void setTranslatePos(float translateX, float translateY){
            this.translateX = translateX;
            this.translateY = translateY;
        }

        public float getTouchPosX(){
            return this.translateX;
        }
        public float getTouchPosY(){
            return this.translateY;
        }

        //set and get current scaleFactor of task
        public float getScaleFactor(){
            return this.scaleFactor;
        }
        public void setScaleFactor(float sFactor){
            this.scaleFactor = sFactor;
        }

        // it return current size of target (actually half width)
        public float getTargetWidth(){
            return appList.get(targetIndex).getSize() * scaleFactor;
        }

        //return current distance between finger and target
        public float getTargetDistance(){
            //get targetPosition
            PVector  targetPosVector = appList.get(targetIndex).getPosition();
            //calculate fingerPosition based on current camera
            PVector  fingerPosVector = new PVector((this.mouseX-translateX)/scaleFactor, (this.mouseY-translateY)/scaleFactor);

            //update New distance
            this.targetDistance = PVector.dist(targetPosVector, fingerPosVector);
            return this.targetDistance;
        }


        public void initApplication(){
            //create JSON Array for result export
            fittsResult = new JSONArray();
            //fill application on window
            for (int i = 40; i < width ; i=i+40){
                for(int j = 40; j < height ; j=j+40){
                    appList.add(new Application(i,j));
                }
            }
        }

        //set one random target
        public void setTarget(){
            targetIndex = PApplet.parseInt(random(0,appList.size()));
            appList.get(targetIndex).setTarget();
        }

        //remove current target
        public void deleteTarget(){
            appList.get(targetIndex).deleteTarget();
            targetIndex = -1;
        }

        //draw or update application
        public void drawApplication(){
            for(Application a : appList){
                a.update();
            }
        }

        //start Fitts Task
        public void startTask(){
            if(taskTrial<60){
                //detach users touch and record data and zoom
                if(touch){
                    recordTrialResult();
                    zoom();
                    println(getTargetDistance());
                }
            }
            //task is done
            else if(taskTrial==60){
                //Create and save json file
                JSONObject t = new JSONObject();
                t.setJSONArray("result",fittsResult);


                /*Data Save*/

                //for Android mode it will be save on DOWNLOADS folder on your android system
                //File myFile =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                //saveJSONObject(t,myFile.toString()+"/P2.json");

                //for non android save on current folder
                //saveJSONObject(t,"./outcomfasde5.json");

                /**********/

            }

            //redraw application based on current camera
            pushMatrix();
            translate(translateX, translateY);
            scale(scaleFactor);
            t.drawApplication();
            popMatrix();

        }

        //record data on JSON file
        public void recordTrialResult(){
            //first time on trial
            if(first){
                //create new JSON object
                fiitsTrialResult = new JSONObject();
                //mark number of trial
                fiitsTrialResult.setInt("trail",taskTrial);
                //set start time and update last updated time
                startTime = millis();
                this.lastStampTime = startTime;
                onTask = true;
                first = false;
            }
            // width and distance value was recorded every 100 millis
            if(millis() - lastStampTime > 100){
                distanceArray.setFloat(arrayIndexCounter,getTargetDistance());
                widthArray.setFloat(arrayIndexCounter,getTargetWidth());
                arrayIndexCounter++;
                //distnaceList.append(getTargetDistance());
                //widthList.append(getTargetWidth());
                lastStampTime = millis();
            }
        }

        //once task id done this function discriminate wheter it was success or not
        public void taskDiscriminator(){
            //println(getTargetDistance());

            //task Success
            if(getTargetDistance() < 15){
                //mark non error on jsonObject
                fiitsTrialResult.setBoolean("Error", false);
                println("success");
                long temp = millis()-startTime;
                //record result
                fiitsTrialResult.setInt("completionTime",(int)temp);
                fiitsTrialResult.setJSONArray("distanceArray",distanceArray);
                fiitsTrialResult.setJSONArray("widthArray",widthArray);
                fittsResult.setJSONObject(taskTrial,fiitsTrialResult);
                //increase trial
                taskTrial++;
                println(taskTrial);
                //increase zoom speed
                if(taskTrial==20){
                    speedFactor = 0.04f;
                }
                else if(taskTrial==40){
                    speedFactor = 0.08f;
                }
                resetTask();
            }
            else{
                //mark error on jsonObject
                fiitsTrialResult.setBoolean("Error", true);
                println("fail");
                //if error -> s
                this.scaleFactor=1;
                this.translateX = 0;
                this.translateY = 0;
            }


        }

        //reset task
        public void resetTask(){
            this.deleteTarget();
            this.setTarget();
            this.scaleFactor=1;
            this.translateX = 0;
            this.translateY = 0;
            this.arrayIndexCounter =0;
            distanceArray = new JSONArray();
            widthArray = new JSONArray();
            first = true;
            onTask = false;
        }

        //zooming
        public void zoom(){

            if(this.touch){
                setScaleFactor( getScaleFactor()+ this.speedFactor);

                setTranslatePos(getTouchPosX() -mouseX * this.speedFactor,
                        getTouchPosY() -mouseY* this.speedFactor);
                //this.count--;
            }
        }
    };
    public void settings() {  size(320,320); }
}