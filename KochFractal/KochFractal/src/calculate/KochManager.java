/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

/**
 *
 * @author juleskreutzer
 */
public class KochManager implements Observer {

    private KochFractal kochfractal;
    private JSF31KochFractalFX mContext;
    private ArrayList<Edge> edgeList;
    
    public KochManager(JSF31KochFractalFX mContext)
    {
        this.mContext = mContext;
        kochfractal = new KochFractal();
        kochfractal.addObserver(this);
        edgeList = new ArrayList<Edge>();
    }
    
    public void ChangeLevel(int nextLevel)
    {
        TimeStamp timer = new TimeStamp();
        kochfractal.setLevel(nextLevel);
        edgeList.clear();
        timer.setBegin();
        kochfractal.generateBottomEdge();
        kochfractal.generateLeftEdge();
        kochfractal.generateRightEdge();
        timer.setEnd();
        mContext.setTextCalc(timer.toString());
        drawEdges();
    }
    
    public void drawEdges()
    {
        TimeStamp timer = new TimeStamp();
        timer.setBegin();
        mContext.clearKochPanel();
        for(Edge edge : edgeList)
        {
            mContext.drawEdge(edge);
        }
        timer.setEnd();
        mContext.setTextDraw(timer.toString());
        mContext.setTextNrEdges(String.valueOf(kochfractal.getNrOfEdges()));
    }

    @Override
    public void update(Observable o, Object arg) {
    Edge edge = (Edge) arg;
    edgeList.add(edge);
    }
    
    
    
}
