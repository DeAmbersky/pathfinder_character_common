package common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mendor.pathfinder.common.CharacterFactory;
import com.mendor.pathfinder.common.DamageInstance;
import com.mendor.pathfinder.common.IDamageProvider;
import com.mendor.pathfinder.common.PathfinderCharacter;
import com.mendor.pathfinder.common.attributes.CharacterAttributeDetails;
import com.mendor.pathfinder.common.attributes.SimpleCharacterAttributeManager;
import com.mendor.pathfinder.common.damageproviders.PhysicalDamageProvider;
import com.mendor.pathfinder.common.damageproviders.WeaponFactory;
import com.mendor.pathfinder.common.pathfinderclasses.CharacterClass;
import com.mendor.pathfinder.common.pathfinderclasses.CharacterClassDetails;
import com.mendor.pathfinder.common.pathfinderclasses.SimpleCharacterClassManager;
import com.mendor.pathfinder.common.races.HumanRace;
import com.mendor.pathfinder.common.skills.SimpleCharacterSkillManager;
import com.mendor.pathfinder.common.types.AttributeType;
import com.mendor.pathfinder.common.types.ClassType;
import com.mendor.pathfinder.common.types.DamageType;
import com.mendor.pathfinder.rolls.RandomRoll;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.awt.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DamageProviderTest {
    @Mock PathfinderCharacter weaponOwner;

    @Test
    public void testWeaponFactory() throws IOException {
        IDamageProvider damageProvider1 = WeaponFactory.getInstance().getWeapon("LongSword");
        IDamageProvider damageProvider2 = WeaponFactory.getInstance().getWeapon("ShortSword");
        assertNotNull(damageProvider1);
        assertNotNull(damageProvider2);
    }

    @Test(expected = IllegalStateException.class)
    public void testWeaponProviderNotSetWeaponOwnerThrowsException() throws IOException {
        IDamageProvider damageProvider = WeaponFactory.getInstance().getWeapon("LongSword");

        damageProvider.setTwoHanded(true);
        DamageInstance damageInstance = damageProvider.doDamage(RandomRoll.roll(20, 3));

        assertTrue(damageInstance.getDamageValue() > 0);
    }

    @Test
    public void testWeaponProvider() throws IOException {
        when(weaponOwner.getAttributeModifier(AttributeType.STRENGTH)).thenReturn(3L);

        IDamageProvider longSword = WeaponFactory.getInstance().getWeapon("LongSword");
        longSword.setTwoHanded(true);
        longSword.setOwner(weaponOwner);

        DamageInstance longSwordDamageInstance = longSword.doDamage(RandomRoll.roll(20, 3));
        assertTrue(longSwordDamageInstance.getDamageValue() > 3);

        IDamageProvider twoHandedAxe = WeaponFactory.getInstance().getWeapon("TwoHandedAxe");
        twoHandedAxe.setOwner(weaponOwner);

        DamageInstance twoHandedAxeDamageInstance = twoHandedAxe.doDamage(RandomRoll.roll(20, 5));
        assertTrue(twoHandedAxeDamageInstance.getDamageValue() > 3);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLongSwordShouldThrowsException() throws IOException {
        final IDamageProvider damageProvider = WeaponFactory.getInstance().getWeapon("Fist");
        //damageProvider.doDamage(1L);

        Set<CharacterClassDetails> classDetails = new HashSet<>();
        Set<CharacterAttributeDetails> attributeDetails = new HashSet<>();

        classDetails.add(new CharacterClassDetails(CharacterClass.getInstance(ClassType.FIGHTER), 4));
        classDetails.add(new CharacterClassDetails(CharacterClass.getInstance(ClassType.RANGER), 3));

        attributeDetails.add(new CharacterAttributeDetails(AttributeType.ENDURANCE, 1111));
        attributeDetails.add(new CharacterAttributeDetails(AttributeType.STRENGTH, 1));
        attributeDetails.add(new CharacterAttributeDetails(AttributeType.AGILITY, 12));
        attributeDetails.add(new CharacterAttributeDetails(AttributeType.WISDOM, 10));
        attributeDetails.add(new CharacterAttributeDetails(AttributeType.INTELLIGENCE, 10));
        attributeDetails.add(new CharacterAttributeDetails(AttributeType.CHARISMA, 12));

        CharacterFactory characterFactory = new CharacterFactory(new SimpleCharacterClassManager(), new SimpleCharacterAttributeManager(), new SimpleCharacterSkillManager());

        PathfinderCharacter  testCharacter = characterFactory.newCharacter(new HumanRace(), classDetails, attributeDetails);

        testCharacter.setName("Vadgast");
        testCharacter.setAge(34);
        testCharacter.setHeight(182);
        testCharacter.setWeight(80);
        testCharacter.setEyeColor(Color.GRAY);
        testCharacter.setHairColor(Color.BLACK);

        damageProvider.setOwner(testCharacter);
        DamageInstance damageInstance = damageProvider.doDamage(2L);
        System.out.println(damageInstance);
    }

    @Test
    public void serializationTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        PhysicalDamageProvider longSword = new PhysicalDamageProvider();

        longSword.setMinDamage(1);
        longSword.setMaxDamage(8);
        longSword.setCriticalChancePercent(5);
        longSword.setCriticalMultiplier(2);
        longSword.setTwoHanded(false);
        longSword.setTwoHandDamageBonus(1.5);
        longSword.setUseAgilityBonus(false);
        longSword.setUseStrengthBonus(true);
        longSword.setDamageType(DamageType.CUTTING);
        longSword.setName("Long Sword");
        longSword.setDescription("Common Long Sword");
        longSword.setBuyCost(5.0);
        longSword.setSellCost(0.6);


        String value = objectMapper.writeValueAsString(longSword);
        assertTrue(!value.isEmpty());
    }

    @Test
    public void deserializationTest() throws IOException {
        List<PhysicalDamageProvider> providers = new ObjectMapper()
                .readValue(getClass().getClassLoader().getResourceAsStream("weapons.json")
                        , new TypeReference<List<PhysicalDamageProvider>>(){});

        assertNotNull(providers);
    }
}
