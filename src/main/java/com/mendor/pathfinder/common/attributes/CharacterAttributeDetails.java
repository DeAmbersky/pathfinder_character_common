package com.mendor.pathfinder.common.attributes;

import com.mendor.pathfinder.common.types.AttributeType;

import java.util.*;

public class CharacterAttributeDetails implements IAttributeNotifier {

    private Long id;
    private AttributeType type;
    private long valueNormal;
    private long modifier;
    private long tempValueBonus;
    private long tempModifierBonus;

    private Set<IAttributeListener> listenersList = new HashSet<>();

    public CharacterAttributeDetails() {
    }

    public CharacterAttributeDetails(AttributeType type, long valueNormal) {
        this.type = type;
        this.valueNormal = valueNormal;
        calculateModifier();
    }

    @Override
    public void addListener(IAttributeListener listener) {
        listenersList.add(listener);
        listener.update(this.valueNormal, this.modifier);
    }

    @Override
    public void notifyListeners() {
        listenersList.forEach(v -> v.update(valueNormal, modifier));
    }

    public long increaseValue(long addValue) {
        valueNormal += addValue;
        modifier = calculateModifier();

        notifyListeners();

        return valueNormal;
    }

    public long decreaseValue(long addValue) {
        valueNormal -= addValue;
        modifier = calculateModifier();

        notifyListeners();

        return valueNormal;
    }

    public void setModifier(long modifier) {
        this.modifier = modifier;
        notifyListeners();
    }

    private long calculateModifier() {
        if (tempModifierBonus == 0)
            return modifier = Math.max(0,(valueNormal + tempValueBonus - 10) / 2);
        else
            return modifier + tempValueBonus;
    }

    public void setTempValueBonus(long value) {
        this.tempValueBonus = value;
    }

    public void setTempModifierBonus(long value) {
        this.tempModifierBonus = value;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public void setValueNormal(long valueNormal) {
        this.valueNormal = valueNormal;
        notifyListeners();
    }

    public long getModifier() {
        return modifier;
    }

    public AttributeType getType() {
        return type;
    }

    public long getValue() {
        return valueNormal + tempModifierBonus;
    }

    public Long getId() {
        return id == null ? -1 : id;
    }

    public CharacterAttributeDetails setId(Long id) {
        this.id = id;
        return this;
    }

    public long getValueNormal() {
        return valueNormal;
    }

    public long getTempValueBonus() {
        return tempValueBonus;
    }

    public long getTempModifierBonus() {
        return tempModifierBonus;
    }
}
