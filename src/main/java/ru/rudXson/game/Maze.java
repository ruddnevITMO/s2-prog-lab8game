package ru.rudXson.game;


import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.*;
import static ru.rudXson.game.EntityType.*;

public class Maze extends GameApplication {

    private Camera3D camera;
    private Entity player;
    private Entity ground;
    private Entity border;
    double boost = 0.001 * 2;
    double flatAcceleration = 0.0005 * 2;
    double hitSlowness = 0.5;

    double borderRadius = 8.7;

    double speedZ = 0;
    double speedX = 0;

    private String lastMove = "";
    private boolean isXframe = true;

    private String side = "flat";
    static ArrayList<ArrayList<Object>> boxes = new ArrayList<>();


    @Override
    protected void initSettings(GameSettings settings) {
        settings.set3D(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Maze");
        settings.setVersion("");
        settings.setAppIcon("images.jpg");
//        settings.setDefaultCursor(new CursorInfo("", 0, 0));

    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.W, () -> this.side = "w");
        onKeyDown(KeyCode.S, () -> this.side = "s");
        onKeyDown(KeyCode.A, () -> this.side = "a");
        onKeyDown(KeyCode.D, () -> this.side = "d");
        onKeyDown(KeyCode.SPACE, () -> this.side = "flat");

    }


    @Override
    protected void onUpdate(double tpf) {
        if (player != null) {
            if (isXframe) {
                if (side.equals("a")) { // x -boost
                    speedX -= boost;
                } else if (side.equals("d")) { // x  boost
                    speedX += boost;
                } else if (side.equals("flat")) {
                    if (speedX > 0) {
                        speedX -= flatAcceleration;
                    } else if (speedX < 0) {
                        speedX += flatAcceleration;
                    }
                }
                if (speedX > 0) {
                    lastMove = "d";
                } else if (speedX < 0) {
                    lastMove = "a";
                }
                player.translateX(speedX);
                if (Math.sqrt(Math.pow(Math.floor(player.getX()) + 0.5, 2) + Math.pow(Math.floor(player.getZ()) + 0.5, 2)) > borderRadius) {
                    player.translateX(-speedX);
                    speedX = 0;
                }
                isXframe = false;
            } else {
                if (side.equals("w")) {      // z  boost
                    speedZ += boost;
                } else if (side.equals("s")) { // z -boost
                    speedZ -= boost;
                } else if (side.equals("flat")) {
                    if (speedZ > 0) {
                        speedZ -= flatAcceleration;
                    } else if (speedZ < 0) {
                        speedZ += flatAcceleration;
                    }
                }
                if (speedZ > 0) {
                    lastMove = "w";
                } else if (speedZ < 0) {
                    lastMove = "s";
                }
                player.translateZ(speedZ);
                if (Math.sqrt(Math.pow(Math.floor(player.getX()) + 0.5, 2) + Math.pow(Math.floor(player.getZ()) + 0.5, 2)) > borderRadius) {
                    player.translateZ(-speedZ);
                    speedZ = 0;
                }
                isXframe = true;
            }


            speedZ = (double) Math.round(speedZ * 1000) / 1000;
            speedX = (double) Math.round(speedX * 1000) / 1000;

        }
    }



    private void initCollisions() {
        onCollisionBegin(SPHERE, BOX, (sphere, box) -> {
            if (Objects.equals(lastMove, "w")) {
                player.translateZ(-speedZ);
                speedZ *= -hitSlowness;
            } else if (Objects.equals(lastMove, "s")) {
                player.translateZ(-speedZ);
                speedZ *= -hitSlowness;
            } else if (Objects.equals(lastMove, "a")) {
                player.translateX(-speedX);
                speedX *= -hitSlowness;
            } else if (Objects.equals(lastMove, "d")) {
                player.translateX(-speedX);
                speedX *= -hitSlowness;
            }

        });

        onCollisionBegin(SPHERE, FINISH, (sphere, finish) -> {
            getDialogService().showMessageBox("You won!", () -> {
                System.exit(0);
            });
        });
    }


    private void spawnBoxes() {
        for (ArrayList<Object> box : boxes) {
            SpawnData data = new SpawnData((double) box.get(1), 4.4, (double) box.get(2));
            data.put("name", box.get(0));
            data.put("areaSquared", box.get(3));
            spawn("box", data);
        }
    }

    @Override
    protected void initGame() {
        runOnce(() -> {

            camera = getGameScene().getCamera3D();

            getGameWorld().addEntityFactory(new MazeFactory());
            getExecutor().startAsyncFX(this::createWorld);

        }, Duration.seconds(0.5));
    }

    private void createWorld() {
        camera.getTransform().translateY(-10);
        camera.getTransform().translateX(5);

        spawnBoxes();

        this.ground = spawn("ground", 0, 5, 0);
        this.border = spawn("border", 0, 4, 0);
        this.border = spawn("finish", 0, 4, 9.15);

        camera.getTransform().lookAt(ground.getPosition3D());

        player = spawn("sphere", 0, 4.3, -8);

        initCollisions();
    }


    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("hits", 0);
    }


    public static void main(String[] args) {
        if (args.length == 0) {
            ArrayList<Object> el = new ArrayList<>();
            el.add("rud");
            el.add(Double.valueOf("2"));
            el.add(Double.valueOf("2"));
            el.add(Double.valueOf("2"));
            boxes.add(el);
        } else {
            for (int i = 0; i < args.length / 4; i++) {
                ArrayList<Object> el = new ArrayList<>();
                int pos = i * 4;
                el.add(args[pos]);
                el.add(Double.valueOf(args[pos + 1]));
                el.add(Double.valueOf(args[pos + 2]));
                el.add(Double.valueOf(args[pos + 3]));
                boxes.add(el);
            }
        }
        launch(args);
    }
}
