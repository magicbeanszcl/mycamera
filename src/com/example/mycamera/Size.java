package com.example.mycamera;

import android.graphics.Point;
import android.hardware.Camera;

public class Size {
	private final Point value;
	public int width(){return value.x;}
	public int height(){return value.y;}

public Size(int width,int height){
	value = new Point(width,height);
}

public Size(Camera.Size size){
	if(size == null){
		value = new Point(0,0);
	}else{
		value = new Point(size.width,size.height);
	}
}

public Size(Point p){
	if(p == null){
		value = new Point(0,0);
	}else{
		value = new Point(p);
	}
}
public Size(Size size) {
	// TODO Auto-generated constructor stub
	if(size == null){
		value = new Point(0,0);
	}else{
	    value = new Point(size.width(),size.height());
	}
}

}
