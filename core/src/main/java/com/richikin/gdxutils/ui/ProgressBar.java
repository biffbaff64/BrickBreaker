package com.richikin.gdxutils.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.richikin.gdxutils.core.GdxApp;
import com.richikin.gdxutils.logging.StopWatch;
import com.richikin.gdxutils.maths.ItemF;

import java.util.concurrent.TimeUnit;

public class ProgressBar extends ItemF implements Disposable
{
    private static final int _DEFAULT_BAR_HEIGHT = 26;
    private static final int _DEFAULT_INTERVAL   = 100;

    public boolean justEmptied;
    public boolean isAutoRefilling;

    private int       subInterval;
    private int       addInterval;
    private float     speed;
    private float     height;
    private float     scale;
    private NinePatch ninePatch;
    private StopWatch stopWatch;

    public ProgressBar(int _speed, int size, int maxSize, String texture)
    {
        ninePatch = new NinePatch(GdxApp.getAssets().getObjectRegion(texture), 1, 1, 1, 1);

        this.minimum         = 0;
        this.maximum         = maxSize;
        this.refillAmount    = 0;
        this.stopWatch       = StopWatch.start();
        this.total           = size;
        this.height          = _DEFAULT_BAR_HEIGHT;
        this.refillAmount    = maxSize;
        this.justEmptied     = false;
        this.isAutoRefilling = false;
        this.scale           = 1;
        this.speed           = _speed;
        this.addInterval     = _DEFAULT_INTERVAL;
        this.subInterval     = _DEFAULT_INTERVAL;
    }

    public void draw(int x, int y, SpriteBatch spriteBatch)
    {
        if (total > 0)
        {
            ninePatch.draw(spriteBatch, x, y, total * scale, height);
        }
    }

    public void updateSlowDecrement()
    {
        justEmptied = false;

        if (total > 0)
        {
            if (stopWatch.time(TimeUnit.MILLISECONDS) >= subInterval)
            {
                total -= speed;

                if (isEmpty())
                {
                    justEmptied = true;
                }

                stopWatch.reset();
            }
        }
    }

    public void updateSlowDecrementWithWrap(int wrap)
    {
        justEmptied = false;

        if (total > 0)
        {
            if (stopWatch.time(TimeUnit.MILLISECONDS) >= subInterval)
            {
                total -= speed;
                total = Math.max(0, total);

                if (isEmpty())
                {
                    total = wrap;
                }

                stopWatch.reset();
            }
        }
    }

    public boolean updateSlowIncrement()
    {
        if (total < maximum)
        {
            if (stopWatch.time(TimeUnit.MILLISECONDS) >= addInterval)
            {
                total += speed;

                stopWatch.reset();
            }
        }

        return isFull();
    }

    public void setHeightColorScale(float height, Color color, float scale)
    {
        this.height = height;
        this.ninePatch.setColor(color);
        this.scale = scale;
    }

    public void setHeight(float _height)
    {
        height = _height;
    }

    public boolean hasRefillRoom()
    {
        return hasRoom() && (total < (getMax() - 10));
    }

    public void setColor(Color color)
    {
        this.ninePatch.setColor(color);
    }

    public float getSpeed()
    {
        return speed;
    }

    public void setSpeed(float _speed)
    {
        this.speed = _speed;
    }

    public void setSubInterval(int _subInterval)
    {
        subInterval = _subInterval;
    }

    public void setAddInterval(int _addInterval)
    {
        addInterval = _addInterval;
    }

    @Override
    public void dispose()
    {
        ninePatch = null;
        stopWatch = null;
    }
}
