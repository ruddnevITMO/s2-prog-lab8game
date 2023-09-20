package ru.rudXson.game;


import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.scene3d.Cone;
import com.almasb.fxgl.scene3d.Cylinder;
import com.almasb.fxgl.scene3d.Torus;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class MazeFactory implements EntityFactory {

    @Spawns("crystal")
    public Entity newCrystal(SpawnData data) {
        var view = new Cone();
        view.setBottomRadius(1);
        view.setTopRadius(0.2);
        view.setHeight(1);

        var e = entityBuilder(data)
                .type(EntityType.CRYSTAL)
                .bbox(BoundingShape.box3D(2, 1, 2))
                .view(view)
                .collidable()
                .with(new ProjectileComponent(new Point2D(0, 1), 1.5).allowRotation(false))
                .build();

        e.setScaleUniform(0);

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .scale(e)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(1, 1, 1))
                .buildAndPlay();

        return e;
    }

    @Spawns("ground")
    public Entity newGround(SpawnData data) {
        var mat = new PhongMaterial(Color.DARKGOLDENROD);

        var view = new Cylinder();
        view.setBottomRadius(10);
        view.setTopRadius(10);
        view.setHeight(0.5);
        view.setMaterial(mat);

        return entityBuilder(data)
                .type(EntityType.GROUND)
                .bbox(BoundingShape.box3D(20, 0.5, 20))
                .view(view)
                .build();
    }

    @Spawns("border")
    public Entity newBorder(SpawnData data) {
        var mat = new PhongMaterial(Color.DARKGOLDENROD);

        var view = new Torus(10, 1);
        view.setRotationAxis(new Point3D(10, 0, 0));
        view.setRotate(90);
        view.setMaterial(mat);

        return entityBuilder(data)
                .type(EntityType.BORDER)
                .bbox(BoundingShape.box3D(20, 0.4, 20))
                .view(view)
                .build();
    }


    @Spawns("sphere")
    public Entity newSphere(SpawnData data) {
        var mat = new PhongMaterial(Color.LIMEGREEN);

        var view = new Sphere();
        view.setRadius(0.5);
        view.setMaterial(mat);

        return entityBuilder(data)
                .type(EntityType.SPHERE)
                .bbox(BoundingShape.box3D(1, 1, 1))
                .view(view)
                .collidable()
                .build();
    }

    @Spawns("box")
    public Entity newPlatform(SpawnData data) {
        var mat = new PhongMaterial(encodeStringToColor(data.get("name")));
        double area = data.get("areaSquared");
        double height = 0.8;
        var view = new Box(area, height, area);
        view.setMaterial(mat);

        return entityBuilder(data)
                .type(EntityType.BOX)
                .bbox(BoundingShape.box3D(area, height, area))
                .view(view)
                .collidable()
                .build();
    }

    @Spawns("finish")
    public Entity newFinish(SpawnData data) {
        var mat = new PhongMaterial(Color.LIME);
        var view = new Box(2, 3, 0.5);
        view.setMaterial(mat);

        return entityBuilder(data)
                .type(EntityType.FINISH)
                .bbox(BoundingShape.box3D(2, 3, 0.5))
                .view(view)
                .collidable()
                .build();
    }

    public static Color encodeStringToColor(String input) {
        int hashCode = input.hashCode();
        int red = (hashCode & 0xFF0000) >> 16;
        int green = (hashCode & 0xFF00) >> 8;
        int blue = hashCode & 0xFF;
        return Color.rgb(red, green, blue);
    }
}
