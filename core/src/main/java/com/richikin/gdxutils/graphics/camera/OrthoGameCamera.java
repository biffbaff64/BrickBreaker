package com.richikin.gdxutils.graphics.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.richikin.gdxutils.exceptions.NotImplementedException;
import com.richikin.gdxutils.graphics.GfxData;
import com.richikin.gdxutils.logging.Trace;
import com.richikin.gdxutils.maths.SimpleVec3F;

public class OrthoGameCamera implements IGameCamera, Disposable
{
    public Viewport           viewport;
    public OrthographicCamera camera;
    public String             name;
    public boolean            isInUse;
    public boolean            isLerpingEnabled;

    private float   defaultZoom;
    public  Vector3 lerpVector;

    public OrthoGameCamera(float _sceneWidth, float _sceneHeight, ViewportType _viewType, String _name)
    {
        Trace.fileInfo();
        Trace.dbg("_sceneWidth : ", _sceneWidth);
        Trace.dbg("_sceneHeight: ", _sceneHeight);
        Trace.dbg("_viewType   : ", _viewType);

        this.name             = _name;
        this.isInUse          = false;
        this.isLerpingEnabled = false;
        this.lerpVector       = new Vector3();

        camera = new OrthographicCamera(_sceneWidth, _sceneHeight);
        camera.position.set(_sceneWidth / 2, _sceneHeight / 2, 0);
        camera.update();

        switch (_viewType)
        {
            case _STRETCH:
            {
                viewport = new StretchViewport
                    (
                        camera.viewportWidth * GfxData._PPM,
                        camera.viewportHeight * GfxData._PPM,
                        camera
                    );
                viewport.apply();
            }
            break;

            case _FIT:
            {
                viewport = new FitViewport
                    (
                        camera.viewportWidth * GfxData._PPM,
                        camera.viewportHeight * GfxData._PPM,
                        camera
                    );
                viewport.apply();
            }
            break;

            case _FILL:
            case _SCREEN:
            {
                throw new NotImplementedException("Type " + _viewType + " not yet supported");
            }

            case _EXTENDED:
            default:
            {
                viewport = new ExtendViewport
                    (
                        camera.viewportWidth * GfxData._PPM,
                        camera.viewportHeight * GfxData._PPM,
                        camera
                    );
                viewport.apply();
            }
            break;
        }

        setZoomDefault(Zoom._DEFAULT_ZOOM);
    }

    @Override
    public void setPosition(SimpleVec3F _position)
    {
        if (isInUse)
        {
            camera.position.x = _position.x;
            camera.position.y = _position.y;
            camera.position.z = _position.z;

            camera.update();
        }
    }

    @Override
    public void setPosition(SimpleVec3F _position, float _zoom)
    {
        if (isInUse)
        {
            camera.position.x = _position.x;
            camera.position.y = _position.y;
            camera.position.z = _position.z;
            camera.zoom += _zoom;

            camera.update();
        }
    }

    @Override
    public void setPosition(SimpleVec3F _position, float _zoom, boolean _shake)
    {
        if (isInUse)
        {
            camera.position.x = _position.x;
            camera.position.y = _position.y;
            camera.position.z = _position.z;
            camera.zoom += _zoom;

            if (_shake)
            {
                Shake.update(Gdx.graphics.getDeltaTime(), camera);
            }

            camera.update();
        }
    }

    @Override
    public Vector3 getPosition()
    {
        return camera.position;
    }

    @Override
    public void updatePosition(float targetX, float targetY)
    {
        if (isInUse)
        {
            Vector3 position = camera.position;

            // a = current camera position
            // b = target
            // a = (b - a) * lerp

            position.x = camera.position.x + (targetX - camera.position.x) * 0.1f;
            position.y = camera.position.y + (targetY - camera.position.y) * 0.1f;

            camera.position.set(position);
            camera.update();
        }
    }

    @Override
    public void lerpTo(SimpleVec3F _position, float _speed)
    {
        if (isInUse && isLerpingEnabled)
        {
            lerpVector.set(_position.x, _position.y, _position.z);

            camera.position.lerp(lerpVector, _speed);
            camera.update();
        }
    }

    @Override
    public void lerpTo(SimpleVec3F _position, float _speed, float _zoom, boolean _shake)
    {
        if (isInUse && isLerpingEnabled)
        {
            lerpVector.set(_position.x, _position.y, _position.z);

            camera.position.lerp(lerpVector, _speed);
            camera.zoom += _zoom;

            if (_shake)
            {
                Shake.update(Gdx.graphics.getDeltaTime(), camera);
            }

            camera.update();
        }
    }

    @Override
    public void resizeViewport(int _width, int _height, boolean _centerCamera)
    {
        viewport.update(_width, _height, _centerCamera);
        camera.update();
    }

    @Override
    public void setCameraZoom(float _zoom)
    {
        camera.zoom = _zoom;
    }

    @Override
    public float getCameraZoom()
    {
        return camera.zoom;
    }

    @Override
    public void setZoomDefault(float _zoom)
    {
        camera.zoom = _zoom;
        defaultZoom = _zoom;
    }

    @Override
    public float getDefaultZoom()
    {
        return defaultZoom;
    }

    @Override
    public void reset()
    {
        camera.zoom = Zoom._DEFAULT_ZOOM;
        camera.position.set(0, 0, 0);
        camera.update();
    }

    @Override
    public void debug()
    {
        Trace.fileInfo();

        Trace.dbg("Name                      : ", name);
        Trace.dbg("camera.viewportWidth      : ", camera.viewportWidth);
        Trace.dbg("camera.viewportHeight     : ", camera.viewportHeight);
        Trace.dbg("viewport.getScreenWidth() : ", viewport.getScreenWidth());
        Trace.dbg("viewport.getScreenHeight(): ", viewport.getScreenHeight());
        Trace.dbg("(PPM)                     : ", GfxData._PPM);
        Trace.dbg("(HUD_WIDTH)               : ", GfxData._HUD_WIDTH);
        Trace.dbg("(HUD_HEIGHT)              : ", GfxData._HUD_HEIGHT);
        Trace.dbg("(HUD_WIDTH / PPM)         : ", GfxData._HUD_WIDTH / GfxData._PPM);
        Trace.dbg("(HUD_HEIGHT / PPM)        : ", GfxData._HUD_HEIGHT / GfxData._PPM);
    }

    @Override
    public void dispose()
    {
        camera     = null;
        viewport   = null;
        name       = null;
        lerpVector = null;
    }
}
