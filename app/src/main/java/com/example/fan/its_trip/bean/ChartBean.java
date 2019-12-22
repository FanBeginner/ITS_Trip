package com.example.fan.its_trip.bean;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/26 0026.
 */

public class ChartBean {
    int maxPoint;
    List<String>xLable=new ArrayList<>();//x轴标签
    List<Entry>yList=new ArrayList<>();//y轴值
    LinkedList<Integer> color=new LinkedList<>();//节点颜色
    LinkedList<Integer> wcolor=new LinkedList<>();//两节点间线段的颜色

    public ChartBean(int maxPoint) {
        this.maxPoint = maxPoint;
        for(int i=0;i<maxPoint;i++){
            xLable.add("");
        }
    }

    public void getLineSet(LineChart lineChart){
        lineChart.setDescription("");
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().enableGridDashedLine(20,20,5);
        lineChart.getAxisRight().setEnabled(false);
    }
    public LineDataSet getLineDataSet(){
        LineDataSet lineDataSet=new LineDataSet(yList,"");
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleColors(color);
        lineDataSet.setValueTextSize(16f);
        wcolor.clear();
        for(int i=0;i<color.size();i++){
            wcolor.add(color.get(i));
        }
        wcolor.poll();
        if(wcolor.size()>0) {
            lineDataSet.setColors(wcolor);
        }
        return lineDataSet;
    }
    public LineData getLineData(){
        LineData lineData=new LineData(xLable,getLineDataSet());
        return lineData;
    }
    public void putData(List<String> stringList,List<Integer>integerList,int max){
        for(int i=0;i<stringList.size();i++){
            xLable.set(i,stringList.get(i));
        }
        yList=new ArrayList<>();//更新数据，不然会乱，出现异常
        color=new LinkedList<>();
        for(int i=0;i<integerList.size();i++){
            if(integerList.get(i)>max){
                color.add(Color.RED);
            }else{
                color.add(Color.GREEN);
            }
            yList.add(new Entry(integerList.get(i),i));
        }
        if(color.size()>maxPoint){
            color.poll();
        }
    }
}
