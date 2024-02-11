package me.iantapply.dungeons.dungeons.generation;

import me.iantapply.dungeons.developerkit.GKBase;

import java.util.*;
import java.util.stream.Collectors;

public class Generator extends GKBase {

    public Generator(Preset preset1) {
        preset = preset1;
        setup();
    }

    Preset preset;

    // TODO: move these floor presets to a different class
    public void presets() {
        sizeX = preset.sizeX;
        sizeY = preset.sizeY;
        switch (preset) {
            case FLOOR7:
                roomMax = new int[]{
                        2, 2, 2, 2, 2, 5, 14 //6, 16, 14
                };
                LRooms = 2;
                amountOfSpecialRooms = 5;
                maxPuzzleRooms = 4;
                lSeparateCount = true;
                break;
            case FLOOR6:
                roomMax = new int[]{
                        1, 2, 2, 2, 2, 5, 13 //3, 16, 13
                };
                LRooms = 1;
                amountOfSpecialRooms = 5;
                maxPuzzleRooms = 4;
                lSeparateCount = true;
                break;
            case FLOOR1:
                roomMax = new int[]{
                        1, 1, 1, 1, 1, 3, 8 //3, 16, 13
                };
                LRooms = 1;
                fairyToBoss = 1;
                spawnToFairy = 1;
                amountOfSpecialRooms = 3;
                maxPuzzleRooms = 3;
                lSeparateCount = true;
                break;
            case FLOOR2:
                roomMax = new int[]{
                        1, 1, 1, 1, 1, 4, 9 //3, 16, 13
                };
                LRooms = 1;
                fairyToBoss = 1;
                spawnToFairy = 2;
                amountOfSpecialRooms = 4;
                maxPuzzleRooms = 3;
                lSeparateCount = true;
                break;
            case FLOOR3:
                roomMax = new int[]{
                        2, 2, 2, 2, 2, 5, 11
                };
                LRooms = 2;
                fairyToBoss = 1;
                spawnToFairy = 2;
                amountOfSpecialRooms = 5;
                maxPuzzleRooms = 3;
                lSeparateCount = false;
                break;
            case FLOOR4:
                roomMax = new int[]{
                        2, 2, 2, 2, 2, 6, 12
                };
                LRooms = 1;
                fairyToBoss = 2;
                spawnToFairy = 2;
                amountOfSpecialRooms = 5;
                maxPuzzleRooms = 3;
                lSeparateCount = false;
                break;
        }
        rooms = new int[sizeY][sizeX][3];
        alreadyOccupied = new boolean[sizeY][sizeX];
        doors = new HashMap<>();
        extendedRooms = new HashMap<>();
        roomsMade = new int[roomMax.length];

        types = new Type[sizeY][sizeX];
        specialRooms = new boolean[sizeY][sizeX];
        specialTypes = new SpecialType[sizeY][sizeX];
        theUsually5Types = new ArrayList<>();
    }

    // TODO: move these to the top for extra organization
    public boolean lSeparateCount = true;

    public int maxPuzzleRooms = 4;

    public int sizeX = 6;
    public int sizeY = 6;

    public HashMap<String, ArrayList<String>> extendedRooms = new HashMap<>();
    //public ArrayList<Tuple> extendedRoomsTest = new ArrayList<>();


    public boolean[][] specialRooms = new boolean[sizeY][sizeX];
    public Type[][] types = new Type[sizeY][sizeX];
    public SpecialType[][] specialTypes = new SpecialType[sizeY][sizeX];

    public int[][][] rooms = new int[sizeY][sizeX][3];
    public boolean[][] alreadyOccupied = new boolean[sizeY][sizeX];

    public int[] roomMax = new int[]{
            2, 2, 2, 2, 2, 5, 14
    };

    public int spawnToFairy = 2;

    public int fairyToBoss = 2;

    public int[] roomsMade = new int[roomMax.length];

    int sideX = 0;
    int sideY = 0;

    int LRooms = 2;
    int amountOfSpecialRooms = 5;

    Directions startRoomDirection;

    public boolean rgbMatches(int x, int y, int r, int g, int b) {
        return rooms[y][x][0] == r && rooms[y][x][1] == g && rooms[y][x][2] == b;
    }

    public void generateRooms() {
        int[] topOrBottom = new int[]{0, sizeY - 1};
        sideY = topOrBottom[(int) random(2)];
        sideX = (int) random(sizeX);
        rooms[sideY][sideX][0] = 0;
        rooms[sideY][sideX][1] = 255;
        rooms[sideY][sideX][2] = 0;

        specialRooms[sideY][sideX] = true;

        alreadyOccupied[sideY][sideX] = true;

        Directions secondRoomDir;
        if (sideY == sizeY - 1) {
            secondRoomDir = Directions.DOWN;
            startRoomDirection = Directions.DOWN;
        } else {
            secondRoomDir = Directions.UP;
            startRoomDirection = Directions.UP;
        }
        final String initialAndSecondRoom = sideX + " " + sideY;
        ArrayList<String> initialAndSecondRoomList = new ArrayList<>();
        sideX += secondRoomDir.x;
        sideY += secondRoomDir.y;
        initialAndSecondRoomList.add(sideX + " " + sideY);
        doors.put(initialAndSecondRoom, initialAndSecondRoomList);


        ArrayList<int[]> secondRoomExtensions = putExtendedRoom(secondRoomDir, 93, 53, 8, false);

        if (secondRoomExtensions == null) return;

        int fourthRoomFairy = (int) random(2);

        ArrayList<int[]> lastRoomExtension = secondRoomExtensions;

        for (int i = 0; i < spawnToFairy + fourthRoomFairy - 1; i++) {
            lastRoomExtension = makeRoom(lastRoomExtension, 93, 53, 8, false);
        }

        if (lastRoomExtension == null) return;

        ArrayList<int[]> fairyRoomExtension = makeRoom(lastRoomExtension, 255, 64, 255, true);

        if (fairyRoomExtension == null) return;

        lastRoomExtension = fairyRoomExtension;

        for (int i = 0; i < fairyToBoss + (int) random(2); i++) {
            lastRoomExtension = makeRoom(lastRoomExtension, 93, 53, 8, false);
        }

        ArrayList<int[]> bossRoomExtension = makeRoom(lastRoomExtension, 255, 0, 0, true);

        if (bossRoomExtension == null || bossRoomExtension.size() == 0) {
            setup();
            return;
        }

        try {
            specialRooms[
                    bossRoomExtension.get(0)[1]][
                    bossRoomExtension.get(0)[0]] = true;

            if (!isOnBorder(bossRoomExtension.get(0)) ||
                    types[bossRoomExtension.get(0)[1]][bossRoomExtension.get(0)[0]] != Type._1x1) {
                setup();
            } else {
                for (int i = 0; i < amountUnclaimed(); i++) {
                    makeBranchRoom();
                }


                int value1 = 16;

                switch (preset) {
                    case FLOOR1:
                        value1 = 9;
                        break;
                    case FLOOR2:
                        value1 = 13;
                        break;
                    case FLOOR4:
                        value1 = 15;
                        break;
                }

                boolean roomAmountCheck = getTotalSpacesTaken() != value1 || roomsMade[5] != amountOfSpecialRooms;

                if (preset == Preset.FLOOR3 || preset == Preset.FLOOR4) {
                    roomAmountCheck = roomsMade[5] != amountOfSpecialRooms;
                } else {
                    if (roomsMade[0] != LRooms) {
                        doNotRestart = false;
                        return;
                    }
                }

                if (roomAmountCheck) {
                    doNotRestart = false;
                    return;
                }

                doNotRestart = true;


                int randomOffset = 0;

                if (preset == Preset.FLOOR6 || preset == Preset.FLOOR4) {
                    while (amountUnclaimed() > 0) {
                        makeBranchRoom();
                    }

                    sizeX++;

                    for (int i = 0; i < sizeY; i++) {
                        rooms[i] = Arrays.copyOf(rooms[i], sizeX);
                        rooms[i][sizeX - 1] = new int[]{255, 255, 255};
                        alreadyOccupied[i] = Arrays.copyOf(alreadyOccupied[i], sizeX);
                        types[i] = Arrays.copyOf(types[i], sizeX);
                        specialTypes[i] = Arrays.copyOf(specialTypes[i], sizeX);
                        specialRooms[i] = Arrays.copyOf(specialRooms[i], sizeX);
                    }

                    if (preset == Preset.FLOOR4) {
                        randomOffset = (int) random(3) * -1;
                    } else {
                        randomOffset = (int) random(2);
                    }
                }

                //System.out.println((maximumEmptySpaces() + randomOffset) + " " + amountUnclaimed() + " " + randomOffset);

                while (amountUnclaimed() > maximumEmptySpaces() + randomOffset) { //19
                    makeBranchRoom();
                }

                boolean failed;

                int puzzleRooms = (int) random(3) + 2;
                if (preset == Preset.FLOOR1 || preset == Preset.FLOOR2) {
                    puzzleRooms = Math.min(getAmountOfRoomsWithOneDoor() - 1, maxPuzzleRooms);
                } else {
                    puzzleRooms = Math.min(puzzleRooms, maxPuzzleRooms);
                }

                for (int i = 0; i < puzzleRooms; i++) {
                    if (preset == Preset.FLOOR6 || preset == Preset.FLOOR4) {
                        boolean allPuzzle = true;
                        //System.out.println("Size: " + sizeX);
                        for (int y = 0; y < sizeY; y++) {
                            if (alreadyOccupied[y][sizeX - 1] && specialTypes[y][sizeX - 1] != SpecialType.PUZZLE) {
                                allPuzzle = false;
                                break;
                            }
                        }
                        failed = assignSpecialRooms(160, 32, 240, SpecialType.PUZZLE, 0, !allPuzzle, sizeX - 1);
                    } else {
                        failed = assignSpecialRooms(160, 32, 240, SpecialType.PUZZLE, 0, false, 0);
                    }
                    if (!failed) {
                        setup();
                        return;
                    }
                }

                if (Integer.parseInt(preset.toString().replace("FLOOR", "")) >= 3) {
                    failed = assignSpecialRooms(255, 165, 0, SpecialType.TRAP, 0, false, 0);

                    if (!failed) {
                        doNotRestart = true;
                        setup();
                        return;
                    }
                }

                failed = assignSpecialRooms(255, 255, 0, SpecialType.MINIBOSS, 0, false, 0);

                if (!failed) {
                    doNotRestart = true;
                    setup();
                    return;
                }


                int specialRoomAmount = 0;
                for (int y = 0; y < sizeY; y++) {
                    for (int x = 0; x < sizeX; x++) {
                        if (specialRooms[y][x]) {
                            specialRoomAmount++;
                        }
                    }
                }

                if (specialRoomAmount > 2) {
                    setup();
                    return;
                }

                //System.out.println("\n");

                for (int y = 0; y < sizeY; y++) {
                    StringBuilder sb = new StringBuilder();
                    for (int x = 0; x < sizeX; x++) {
                        sb.append(types[y][x]).append(", ");
                    }
                    //System.out.println(sb);
                }

               // System.out.println("SUCCESS!");

                //System.out.println(Arrays.toString(roomsMade));

                if (!checkSum()) {
                    //System.out.println("CHECKSUM FAILED!");
                    setup();
                    return;
                }

                //System.out.println("ding");

                generatedSuccessfully = true;
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            setup();
        }
    }

    ArrayList<Type> theUsually5Types = new ArrayList<>();

    public int amountOfPuzzleRooms() {
        int amount = 0;
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                if (specialTypes[y][x] == SpecialType.PUZZLE) {
                    amount++;
                }
            }
        }
        return amount;
    }

    public boolean generatedSuccessfully = false;

    public int getAmountOfRoomsWithOneDoor() {
        int amount = 0;
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                if (amountOfDoorsConnected(x, y) == 1 && types[y][x] == Type._1x1 && !specialRooms[y][x]) {
                    amount++;
                }
            }
        }
        return amount;
    }

    public boolean doNotRestart = false;

    public int maximumEmptySpaces() {
        int x = sizeX;
        int y = sizeY;
        //if (preset == Preset.FLOOR6 || preset == Preset.FLOOR4) x++;
        int totalRooms;
        switch (preset) {
            case FLOOR1:
                totalRooms = x * y - 9;
                break;
            case FLOOR2:
                totalRooms = x * y - 13;
                break;
            case FLOOR7:
            case FLOOR6:
                totalRooms = x * y - 16;
                break;
            case FLOOR3:
                totalRooms = x * y - 14;
                break;
            default:
                totalRooms = x * y;
                break;
        }

        /*
        roomMax = new int[]{
                        2, 2, 2, 2, 2, 6, 12
                };
         */

        /*for (int i = 0; i < roomMax.length; i++) {
            int room = roomMax[i];
            switch (i) {
                case 0:
                case 3:
                    //totalRooms -= room * 3; //3
                    break;
                case 1:
                    if (!lSeparateCount) {
                        totalRooms -= room * 2; //3
                    }
                    break;
                case 2:
                case 4:
                    if (!lSeparateCount) {
                        totalRooms -= room * 4; //2
                    }
                case 6:
                    //totalRooms -= room; //13
                    break;
                default:
                    break;
            }
        }
         */
        if (preset != Preset.FLOOR4) {
            for (int i = 0; i < roomMax.length; i++) {
                int room = roomMax[i];
                switch (i) {
                    case 0:
                        if (lSeparateCount) {
                            totalRooms -= room * 3; //3
                        }
                        break;
                    case 6:
                        totalRooms -= room; //13
                        break;
                    default:
                        break;
                }
            }
        } else {
            totalRooms = x * y - 17;

            for (int i = 0; i < roomMax.length; i++) {
                int room = roomMax[i];
                if (i == 6) {
                    totalRooms -= room; //13
                }
            }
        }
        return totalRooms;
    }

    public int amountUnclaimed() {
        int amount = 0;
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                if (!alreadyOccupied[i][j]) {
                    amount++;
                }
            }
        }
        return amount;
    }


    public void makeBranchRoom() {
        ArrayList<int[]> surroundingRooms = getAllSurroundingRooms();
        Collections.shuffle(surroundingRooms);
        int randomRoomRandom = (int) random(surroundingRooms.size());
        int[] randomRoom = null;
        for (int i = 0; i < 1; i++) {
            try {
                randomRoom = surroundingRooms.get(randomRoomRandom);
            } catch (IndexOutOfBoundsException e) {
                randomRoomRandom = (int) random(surroundingRooms.size());
                i--;
            }
        }

        int x = randomRoom[0];
        int y = randomRoom[1]; //Initial Room Selected

        ArrayList<int[]> neighbouringAlreadyClaimed = //The neighbouring rooms that are already claimed
                getSurroundingRooms(x, y, false, true);
        Collections.shuffle(neighbouringAlreadyClaimed);
        int randomNeighbour = (int) random(neighbouringAlreadyClaimed.size());
        int[] randomNeighbourRoom = neighbouringAlreadyClaimed.get(randomNeighbour); //A random surrounding room that is already claimed

        sideX = x;
        sideY = y;

        ArrayList<int[]> randomExtensions = putExtendedRoom( //This is the room with extensions going from the chosen to the random neighbour room
                directionGoingIn(x, y, randomNeighbourRoom[0],
                        randomNeighbourRoom[1]),
                93, 53, 8, false);

        if (randomExtensions == null) return;

        ArrayList<String> randomExtensionsString = doors.getOrDefault(co(randomNeighbourRoom[0], randomNeighbourRoom[1]), new ArrayList<>()); //get the doors of the random neighbour room

        String co = co(x, y); //the coordinates of the room with extensions

        randomExtensionsString.add(co);

        doors.put(co(randomNeighbourRoom[0], randomNeighbourRoom[1]), randomExtensionsString);

    }

    public boolean isOnBorder(int[] room) {
        return (room[0] == 0 || room[0] == sizeX - 1 || room[1] == 0 || room[1] == sizeY - 1);
    }

    public ArrayList<int[]> makeRoom(ArrayList<int[]> lastRoomExtensions, int r, int g, int b, boolean fairy) {
        ArrayList<Directions> thirdRoomDirections = new ArrayList<>(Arrays.asList(goingInX(sideX), goingInY(sideY)));
        for (int i = 0; i < thirdRoomDirections.size(); i++) {
            if (lastRoomExtensions != null) {
                for (int[] extension : lastRoomExtensions) {
                    try {
                        if (alreadyOccupied[extension[1] + thirdRoomDirections.get(i).y][extension[0] + thirdRoomDirections.get(i).x]) {
                            thirdRoomDirections.set(i, getOppositeDirection(thirdRoomDirections.get(i)));
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }
            } else {
                setup();
                return null;
            }
        }

        Directions thirdRoomDirection = thirdRoomDirections.get((int) random(2));

        ArrayList<int[]> secondRoomSurrounding = getSurroundingRooms(sideX, sideY, true, false);

        int[] thirdRoomCords = null;

        ArrayList<int[]> getThirdClosestRooms = getClosest3Rooms(secondRoomSurrounding);
        Collections.reverse(getThirdClosestRooms);

        for (int[] cords : getThirdClosestRooms) {
            if (thirdRoomDirections.contains(directionGoingIn(sideX, sideY, cords[0], cords[1]))) {
                thirdRoomCords = cords;
            }
        }
        if (thirdRoomCords == null) {
            try {
                thirdRoomCords = getThirdClosestRooms.get(0);
            } catch (Exception ex) {
                setup();
                return new ArrayList<>();
            }
        }

        sideX = thirdRoomCords[0];
        sideY = thirdRoomCords[1];

        ArrayList<int[]> thirdRoomExtensions = putExtendedRoom(thirdRoomDirection, r, g, b, fairy);

        if (thirdRoomExtensions == null) return null;

        ArrayList<int[]> surroundingRoomsThird = getSurroundingRooms(sideX, sideY, false, false);

        int[] closestRoomClaimedBySecond = null;


        String closestRoomClaimedByThird = null;

        for (int[] surroundingRoom : surroundingRoomsThird) {
            for (int[] secondRoomExtension : lastRoomExtensions) {
                if (surroundingRoom[0] == secondRoomExtension[0] && surroundingRoom[1] == secondRoomExtension[1]) {
                    closestRoomClaimedBySecond = surroundingRoom;
                    break;
                }
            }
        }

        if (closestRoomClaimedBySecond != null) {

            for (int[] surroundingRoom : getSurroundingRooms(closestRoomClaimedBySecond[0], closestRoomClaimedBySecond[1], false, false)) {
                for (int[] secondRoomExtension : thirdRoomExtensions) {
                    if (surroundingRoom[0] == secondRoomExtension[0] && surroundingRoom[1] == secondRoomExtension[1]) {
                        closestRoomClaimedByThird = co(surroundingRoom[0], surroundingRoom[1]);
                        break;
                    }
                }
            }

            if (closestRoomClaimedByThird != null) {
                doors.put(closestRoomClaimedByThird, new ArrayList<>(Collections.singletonList(co(closestRoomClaimedBySecond[0], closestRoomClaimedBySecond[1]))));
            }
        }

        return thirdRoomExtensions;
    }

    public boolean assignSpecialRooms(int r, int g, int b, SpecialType specialType, int timesRan, boolean isSetX, int setX) {
        if (timesRan >= 100) {
            return false;
        } else {
            int[] cords;
            if (isSetX) {
                cords = new int[]{setX, (int) random(sizeY)};
            } else {
                cords = new int[]{(int) random(sizeX), (int) random(sizeY)};
            }
            if (amountOfDoorsConnected(cords[0], cords[1]) == 1
                    && types[cords[1]][cords[0]] == Type._1x1
                    && specialTypes[cords[1]][cords[0]] == null
                    && !specialRooms[cords[1]][cords[0]]) {
                rooms[cords[1]][cords[0]][0] = r;
                rooms[cords[1]][cords[0]][1] = g;
                rooms[cords[1]][cords[0]][2] = b;

                specialTypes[cords[1]][cords[0]] = specialType;
                return true;
            } else {
                return assignSpecialRooms(r, g, b, specialType, timesRan + 1, isSetX, setX);
            }
        }
    }

    public ArrayList<int[]> putExtendedRoom(Directions direction, int r, int g, int b, boolean fairy) {
        String key = null;
        ArrayList<String> extendedRoomsThird = new ArrayList<>();

        ArrayList<int[]> roomOffset;

        if (!fairy) {
            roomOffset = getOffsetRooms(randomType(7), direction, sideX, sideY, 0);
        } else {
            roomOffset = getOffsetRooms(Type._1x1, direction, sideX, sideY, 0);
        }

        if (roomOffset == null) {
            return null;
        }


        for (int[] cords : roomOffset) {

            int x = sideX + cords[0];
            int y = sideY + cords[1];
            try {
                rooms[y][x][0] = r;
                rooms[y][x][1] = g;
                rooms[y][x][2] = b;
                if (key == null) {
                    key = x + " " + y;
                } else {
                    extendedRoomsThird.add(x + " " + y);
                }
                alreadyOccupied[y][x] = true;
            } catch (Exception ex) {
                setup();
                break;
            }
        }
        extendedRooms.put(key, extendedRoomsThird);
        ArrayList<int[]> extendedRooms = new ArrayList<>();
        for (String cords : extendedRoomsThird) {
            extendedRooms.add(new int[]{Integer.parseInt(cords.split(" ")[0]), Integer.parseInt(cords.split(" ")[1])});
        }
        if (key != null) {
            extendedRooms.add(oc(key));
        }
        return extendedRooms;
    }

    public void listExtendedRooms() {
        for (String key : extendedRooms.keySet()) {
            ArrayList<String> test = extendedRooms.get(key);
            test = (ArrayList<String>) test.stream().distinct().collect(Collectors.toList());
            System.out.println(key + ": " + test);
        }
    }

    public Directions directionGoingIn(int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            if (y1 > y2) return Directions.DOWN;
            else return Directions.UP;
        } else {
            if (x1 > x2) return Directions.LEFT;
            else return Directions.RIGHT;
        }
    }

    public int distance(int x, int y, double randomMidX, double randomMidY) {
        //get distance from [x, y] to [size / 2, size / 2]

        return (int) Math.sqrt(Math.pow(x - randomMidX, 2) + Math.pow(y - randomMidY, 2));
    }

    public float randomRound(double n) {
        int r = (int) random(2);
        if (r == 0) {
            return (int) Math.floor(n);
        } else {
            return (int) Math.ceil(n);
        }
    }

    public ArrayList<int[]> getClosest3Rooms(ArrayList<int[]> rooms) {
        double randomMidX = randomRound((float) (sizeX - 1) / 2);
        double randomMidY = randomRound((float) (sizeY - 1) / 2);
        boolean minus = (int) random(2) == 0;
        double smallOffset = minus ? -0.1 : 0.1;
        while (co((int) randomMidX, (int) randomMidY).equals(co(sideX, sideY))) {
            randomMidX = randomRound((float) (sizeX - 1) / 2 + smallOffset);
            randomMidY = randomRound((float) (sizeY - 1) / 2 + smallOffset);
        }

        double finalRandomMidY = randomMidY;
        double finalRandomMidX = randomMidX;
        rooms.sort((a, b) -> {
            int aDist = distance(a[0], a[1], finalRandomMidX, finalRandomMidY);
            int bDist = distance(b[0], b[1], finalRandomMidX, finalRandomMidY);
            return aDist - bDist;
        });
        ArrayList<int[]> returnList;
        try {
            returnList = new ArrayList<>(rooms.subList(0, 3));
        } catch (Exception ex) {
            returnList = rooms;
        }
        return returnList;
    }

    public HashMap<String, ArrayList<String>> doors = new HashMap<>();

    public int scale = 24;

    public Directions getOtherDir(Directions dir) {
        int random = (int) random(2);
        switch (dir) {
            case DOWN:
            case UP:
                if (random == 0) {
                    return Directions.RIGHT;
                } else {
                    return Directions.LEFT;
                }
            case RIGHT:
            case LEFT:
                if (random == 0) {
                    return Directions.UP;
                } else {
                    return Directions.DOWN;
                }
        }
        return null;
    }

    public Directions getOppositeDirection(Directions dir) {
        //get opposite direction
        switch (dir) {
            case DOWN:
                return Directions.UP;
            case UP:
                return Directions.DOWN;
            case RIGHT:
                return Directions.LEFT;
            case LEFT:
                return Directions.RIGHT;
        }
        return null;
    }

    public int amountOfDoorsConnected(int x, int y) {
        int amount = 0;
        for (String door : doors.keySet()) {
            if (door.equals(co(x, y))) {
                amount++;
            }
            for (String room : doors.get(door)) {
                if (room.equals(co(x, y))) {
                    amount++;
                }
            }
        }
        return amount;
    }

    public Type randomType(int weighted) {
        int random = (int) random(weighted);
        switch (random) {
            default:
                return Type._1x1;
            case 1:
                return Type._2x2;
            case 2:
                return Type._L1;
            case 3:
                return Type._L2;
            case 4:
                return Type._2x1;
            case 5:
                return Type._3x1;
            case 6:
                return Type._4x1;
        }
    }

    public ArrayList<int[]> getExtendedRooms(int x, int y) {
        ArrayList<int[]> list = arrayList(new int[]{x, y});
        for(String key : extendedRooms.keySet()) {
            if(extendedRooms.get(key).contains(co(x, y)) || key.equals(co(x, y))) {
                extendedRooms.get(key).forEach(cords -> list.add(oc(cords)));
                return list;
            }
        }
        return list;
    }

    public int[] getSpawnRoom() {
        for(int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (rgbMatches(x, y, 0, 255, 0)) return new int[]{x, y};
            }
        }
        return new int[]{0, 0};
    }

    public int[] getMiddlePieceOfL(ArrayList<int[]> list) {
        ArrayList<Integer> xs = new ArrayList<>();
        ArrayList<Integer> ys = new ArrayList<>();

        ArrayList<int[]> trimmedList = new ArrayList<>();
        for(int[] cords : list) {
            boolean add = true;
            for(int[] cords2 : trimmedList) {
                if(Arrays.equals(cords, cords2)) {
                    add = false;
                    break;
                }
            }
            if(add) {
                trimmedList.add(cords);
            }
        }

        for (int[] cords : trimmedList) {
            xs.add(cords[0]);
            ys.add(cords[1]);
        }
        xs.sort((a, b) -> {
            int aCount = Collections.frequency(xs, a);
            int bCount = Collections.frequency(xs, b);
            return bCount - aCount;
        });
        ys.sort((a, b) -> {
            int aCount = Collections.frequency(ys, a);
            int bCount = Collections.frequency(ys, b);
            return bCount - aCount;
        });
        return new int[]{xs.get(0), ys.get(0)};
    }



    public ArrayList<int[]> getSurroundingRooms(int x, int y, boolean alreadyClaimed, boolean notClaimed) {
        ArrayList<int[]> surroundingRooms = new ArrayList<>();

        ArrayList<String> rooms = extendedRooms.get(co(x, y));
        if (rooms == null) {
            rooms = new ArrayList<>();
        }
        rooms.add(co(x, y));
        for (String room : rooms) {
            int extendedX = Integer.parseInt(room.split(" ")[0]);
            int extendedY = Integer.parseInt(room.split(" ")[1]);

            for (int i = 0; i < 4; i++) {
                int[] cords = new int[2];
                int xOffset = 0;
                int yOffset = 0;
                switch (i) {
                    case 0:
                        xOffset = -1;
                        break;
                    case 1:
                        xOffset = 1;
                        break;
                    case 2:
                        yOffset = -1;
                        break;
                    case 3:
                        yOffset = 1;
                        break;
                }
                cords[0] = extendedX + xOffset;
                cords[1] = extendedY + yOffset;
                try {
                    if (!alreadyClaimed && !notClaimed) {
                        surroundingRooms.add(cords);
                    } else if (alreadyClaimed && !notClaimed && !rooms.contains(co(cords[0], cords[1])) && !alreadyOccupied[cords[1]][cords[0]]) {
                        surroundingRooms.add(cords);
                    } else if (!alreadyClaimed && alreadyOccupied[cords[1]][cords[0]] && !specialRooms[cords[1]][cords[0]]) {
                        surroundingRooms.add(cords);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        for (int i = 0; i < surroundingRooms.size(); i++) {
            int[] cords = surroundingRooms.get(i);
            for (int[] cords2 : new ArrayList<>(surroundingRooms)) {
                if (i != surroundingRooms.indexOf(cords2)) {
                    if (cords[0] == cords2[0] && cords[1] == cords2[1]) {
                        surroundingRooms.remove(cords);
                    }
                }
            }
        }

        return surroundingRooms;
    }

    public ArrayList<int[]> getOffsetRooms(Type t, Directions dir, int x, int y, int timesRan) {
        try {
            if (timesRan >= 250) {
                setup();
            } else {
                ArrayList<int[]> cords = new ArrayList<>(Collections.singletonList(new int[]{0, 0}));
                for (int i : getRoomsSlotInArray(t)) {
                    try {
                        if (roomsMade[i] >= roomMax[i]) {
                            return getOffsetRooms(randomType(7), dir, x, y, timesRan + 1);
                        }
                    } catch (Exception ex) {
                        setup();
                    }
                }

                switch (t) {
                    case _1x1:
                        break;
                    case _L1:
                        Directions theOtherDir = getOtherDir(dir);
                        try {
                            if (alreadyOccupied[y + dir.y][x + theOtherDir.x] || //y - 1, 0 -1
                                    alreadyOccupied[y + theOtherDir.y][x + theOtherDir.x] ||
                                    y + dir.y < 0 || y + theOtherDir.y < 0 ||
                                    x + theOtherDir.x < 0) {
                                return getOffsetRooms(randomType(7), dir, x, y, timesRan + 1);
                            }
                        } catch (Exception ex) {
                            return getOffsetRooms(randomType(7), dir, x, y, timesRan + 1);
                        }
                        cords.add(new int[]{theOtherDir.x, dir.y});
                        cords.add(new int[]{theOtherDir.x, theOtherDir.y});
                        break;
                    case _L2:
                        theOtherDir = getOtherDir(dir);
                        try {
                            if (alreadyOccupied[y + dir.y][x + dir.x] ||
                                    alreadyOccupied[y + theOtherDir.y][x + theOtherDir.x] ||
                                    y + dir.y < 0 || y + theOtherDir.y < 0 ||
                                    x + dir.x < 0 || x + theOtherDir.x < 0) {
                                return getOffsetRooms(randomType(7), dir, x, y, timesRan + 1);
                            }
                        } catch (Exception ex) {
                            return getOffsetRooms(randomType(7), dir, x, y, timesRan + 1);
                        }
                        cords.add(new int[]{dir.x, dir.y});
                        cords.add(new int[]{theOtherDir.x, theOtherDir.y});
                        break;
                    case _2x2:
                        theOtherDir = getOtherDir(dir);
                        try {
                            if (alreadyOccupied[y + dir.y][x + dir.x] ||
                                    alreadyOccupied[y + theOtherDir.y][x + theOtherDir.x] ||
                                    alreadyOccupied[y + theOtherDir.y][x + dir.x]) {
                                return getOffsetRooms(randomType(7), dir, x, y, timesRan + 1);
                            }
                        } catch (Exception ex) {
                            return getOffsetRooms(randomType(7), dir, x, y, timesRan + 1);
                        }
                        cords.add(new int[]{dir.x, dir.y});                 //x  Main
                        cords.add(new int[]{theOtherDir.x, theOtherDir.y}); //xx
                        cords.add(new int[]{theOtherDir.x, dir.y});
                        break;
                    case _2x1:
                    case _3x1:
                    case _4x1:
                        Directions[] x1s = new Directions[]{Directions.RIGHT, Directions.LEFT, dir};
                        List<Directions> x1sL = new ArrayList<>(Arrays.asList(x1s));
                        Collections.shuffle(x1sL);
                        boolean reallyCant = true;
                        ArrayList<Directions> workingDirs = new ArrayList<>();
                        for (Directions dirs : x1sL) {
                            boolean cant = false;
                            for (int i = 1; i < t.distance + 1; i++) {
                                try {
                                    if (alreadyOccupied[y + dirs.y * i][x + dirs.x * i]) {
                                        cant = true;
                                        break;
                                    }
                                } catch (Exception ex) {
                                    cant = true;
                                    break;
                                }
                            }
                            if (cant) {
                                continue;
                            }
                            reallyCant = false;
                            workingDirs.add(dirs);

                        }
                        if (!reallyCant) {
                            Directions dirs = workingDirs.get((int) random(workingDirs.size() - 1));
                            if (dirs != Directions.UP && dirs != Directions.DOWN && (t == Type._3x1 || t == Type._4x1)) {
                                Directions thisOneDir;
                                if (dirs == Directions.RIGHT) thisOneDir = Directions.LEFT;
                                else thisOneDir = Directions.RIGHT;
                                int thisOneRandom = (int) Math.floor(random(2));
                                switch (thisOneRandom) {
                                    case 0:
                                        boolean doo = false;
                                        for (int i = 1; i < t.distance + 1; i++) {
                                            try {
                                                if (alreadyOccupied[y + dirs.y * i + thisOneDir.y][x + dirs.x * i + thisOneDir.x] ||
                                                        alreadyOccupied[y + thisOneDir.y][x + thisOneDir.x]) {
                                                    doo = true;
                                                    break;
                                                }
                                            } catch (Exception ex) {
                                                doo = true;
                                                break;
                                            }
                                        }
                                        if (!doo) {
                                            for (int i = 1; i < t.distance + 1; i++) {
                                                if (dirs.x * i + thisOneDir.x != 0 || dirs.y * i + thisOneDir.y != 0) {
                                                    cords.add(new int[]{dirs.x * i + thisOneDir.x, dirs.y * i + thisOneDir.y});
                                                }
                                            }
                                            break;
                                        }
                                    case 1:
                                        for (int i = 1; i < t.distance + 1; i++) {
                                            cords.add(new int[]{dirs.x * i, dirs.y * i});
                                        }
                                        break;
                                }
                            } else {
                                for (int i = 1; i < t.distance + 1; i++) {
                                    cords.add(new int[]{dirs.x * i, dirs.y * i});
                                }
                            }

                        } else {
                            return getOffsetRooms(randomType(7), dir, x, y, timesRan + 1);
                        }
                }
                for (int i : getRoomsSlotInArray(t)) {
                    ;
                    roomsMade[i]++;
                }

                for (int[] cord : cords) {
                    for (int[] cord2 : cords) {
                        if (cord != cord2 && cord[0] == cord2[0] && cord[1] == cord2[1]) {
                            for (int i : getRoomsSlotInArray(t)) {
                                roomsMade[i]--;
                            }
                            return getOffsetRooms(randomType(7), dir, x, y, timesRan + 1);
                        }
                    }
                    types[y + cord[1]][x + cord[0]] = t;
                }
                theUsually5Types.add(t);
                //TODO: save special rooms types (2x2, 3x1, etc) to arraylist to retrieve to get amount for maximum empty rooms
                return cords;
            }
            return null;
        } catch (StackOverflowError e) {
            setup();
            return null;
        }
    }

    public boolean checkSum() {
        int[] sum = new int[roomsMade.length];
        for (int i = 0; i < roomsMade.length; i++) {
            switch (i) {
                case 0:
                case 3:
                    sum[i] = roomsMade[i] * 3;
                    break;
                case 2:
                case 4:
                    sum[i] = roomsMade[i] * 4;
                    break;
                case 1:
                    sum[i] = roomsMade[i] * 2;
                    break;
                case 6:
                    sum[i] = roomsMade[i];
                    break;
                default:
                    break;
            }
        }

        int[] sum2 = new int[roomsMade.length];
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                int i = 0;
                if (types[y][x] != null) {
                    switch (types[y][x]) {
                        case _L1:
                        case _L2:
                            break;
                        case _2x1:
                            i = 1;
                            break;
                        case _2x2:
                            i = 2;
                            break;
                        case _3x1:
                            i = 3;
                            break;
                        case _4x1:
                            i = 4;
                            break;
                        default:
                            i = 6;
                            break;
                    }
                    sum2[i]++;
                }
            }
        }

        //System.out.println("sum: " + Arrays.toString(sum));
        //System.out.println("sum2: " + Arrays.toString(sum2));

        return Arrays.equals(sum, sum2);
    }

    public ArrayList<int[]> getAllSurroundingRooms() {
        ArrayList<int[]> cords = new ArrayList<>();
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                if (alreadyOccupied[y][x] && !specialRooms[y][x]) {
                    cords.addAll(getSurroundingRooms(x, y, true, false));
                }
            }
        }
        ArrayList<int[]> finalCords = new ArrayList<>();
        for (int[] cord : new ArrayList<>(cords)) {
            boolean doo = false;
            for (int[] finalCord : finalCords) {
                if (Arrays.equals(finalCord, cord)) {
                    doo = true;
                    break;
                }
            }
            if (!doo) {
                finalCords.add(cord);
            }
        }

        return finalCords;
    }

    public ArrayList<Integer> getRoomsSlotInArray(Type t) {
        ArrayList<Integer> list = new ArrayList<>();
        switch (t) {
            case _1x1:
                list.add(6);
                return list;
            case _2x1:
                list.add(1);
                list.add(5);
                return list;
            case _2x2:
                list.add(2);
                list.add(5);
                return list;
            case _3x1:
                list.add(3);
                list.add(5);
                return list;
            case _4x1:
                list.add(4);
                list.add(5);
                return list;
            default:
                list.add(0);
                return list;
        }
    }

    public int getTotalSpacesTaken() {
        int total = 0;
        for (int x = 0; x < roomsMade.length; x++) {
            int i = roomsMade[x];
            switch (x) {
                case 1:
                    total += 2 * i;
                    break;
                case 2:
                case 4:
                    total += 4 * i;
                    break;
                case 3:
                    total += 3 * i;
                    break;
                default:
                    break;
            }
        }
        return total;
    }

    public Directions goingInX(int x) {
        double center = (double) sizeX / 2;
        if (x >= center) {
            return Directions.LEFT;
        } else {
            return Directions.RIGHT;
        }
    }

    public Directions goingInY(int y) {
        double center = (double) sizeY / 2;
        if (y >= center) {
            return Directions.DOWN;
        } else if (y < center) {
            return Directions.UP;
        } else {
            return startRoomDirection;
        }
    }

    public String co(int x, int y) {
        return x + " " + y;
    }

    public int[] oc(String s) {
        String[] split = s.split(" ");
        return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
    }

    public void setup() {
        presets();
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                rooms[y][x][0] = 255;
                rooms[y][x][1] = 255;
                rooms[y][x][2] = 255;
            }
        }
        generateRooms();
    }
}