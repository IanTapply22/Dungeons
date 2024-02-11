package me.iantapply.dungeons.developerkit.runnable;

import me.iantapply.dungeons.developerkit.GKBase;
import me.iantapply.dungeons.developerkit.utils.duplet.Duplet;
import me.iantapply.dungeons.developerkit.utils.duplet.Quartet;
import me.iantapply.dungeons.developerkit.utils.duplet.Tuple;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public class Condition extends GKBase {

    public Conditions con;

    public Condition(Conditions con) {
        this.con = con;
    }

    public ArrayList<Quartet<?, ?, Operators, Conditions>> operation = new ArrayList<>();

    public <T, K> void add(T one, K two, Operators operator, Conditions con) {
        if(operator == Operators.CUSTOM) return;
        operation.add(Tuple.of(one, two, operator, con));
    }

    public <T, K> void addCustom(Method m, T provider, K affected) {
        //TODO: Write lmao
    }

    public boolean compute() {
        ArrayList<Duplet<Boolean, Conditions>> computed = new ArrayList<>(); //fix for just one in list
        for (Quartet<?, ?, Operators, Conditions> op : operation) {
            Object first = op.getFirst();
            Object second = op.getSecond();
            Operators third = op.getThird();
            Conditions fourth = op.getFourth();

            Duplet<Boolean, Conditions> currentDup;
            boolean boo = false;

            switch (third) {
                case NOTEQUALS:
                case EQUALS:
                    boo = Objects.equals(first, second);
                    if(third == Operators.NOTEQUALS) boo = !boo;
                    break;
                case GREATEREQUAL:
                case GREATER:
                case LESS:
                case LESSEQUAL:
                    if(isNumeric(first) &&
                            isNumeric(second)) {
                        double firstI = (double) first;
                        double secondI = (double) second;

                        switch (third) {
                            case GREATER:
                                boo = firstI > secondI;
                                break;
                            case GREATEREQUAL:
                                boo = firstI >= secondI;
                                break;
                            case LESS:
                                boo = firstI < secondI;
                                break;
                            case LESSEQUAL:
                                boo = firstI <= secondI;
                                break;
                            default: break;
                        }

                    }
                default: break;
            }
            currentDup = Tuple.of(boo, fourth);
            computed.add(currentDup);
        }

        boolean first = computed.get(0).getFirst();
        boolean second = computed.get(1).getFirst();
        Conditions firstCon = computed.get(0).getSecond();
        computed.remove(0);

        boolean returnValue = firstCon == Conditions.AND ?
                Boolean.logicalAnd(first, second) :
                Boolean.logicalOr(first, second);

        for(Duplet<Boolean, Conditions> ops : computed) {
            boolean value = ops.getFirst();
            Conditions andOr = ops.getSecond();
            returnValue = andOr == Conditions.AND ?
                    Boolean.logicalAnd(returnValue, value) :
                    Boolean.logicalOr(returnValue, value);
        }

        return returnValue;
    }
}
