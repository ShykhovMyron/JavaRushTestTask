package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.PlayerEntity;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exeption.IdNotValidException;
import com.game.exeption.PlayerNotFountException;
import com.game.model.NewPlayer;
import com.game.model.UpdatePlayer;
import com.game.repository.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepo playerRepo;

    private static List<PlayerEntity> sortList(List<PlayerEntity> list, PlayerOrder order) {
        switch (order) {
            case ID:
                list.sort((s1, s2) -> (int) (s1.getId() - s2.getId()));
                break;
            case NAME:
                list.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
                break;
            case LEVEL:
                list.sort((s1, s2) -> s1.getLevel() - s2.getLevel());
                break;
            case BIRTHDAY:
                list.sort((s1, s2) -> s1.getBirthday().compareTo(s2.getBirthday()));
                break;
            case EXPERIENCE:
                list.sort((s1, s2) -> s1.getExperience() - s2.getExperience());
                break;
        }
        return list;
    }


    public List<PlayerEntity> getPlayers(String name,
                                         String title,
                                         Race race,
                                         Profession profession,
                                         Long after,
                                         Long before,
                                         Boolean banned,
                                         Integer minExperience,
                                         Integer maxExperience,
                                         Integer minLevel,
                                         Integer maxLevel,
                                         PlayerOrder order,
                                         Integer pageNumber,
                                         Integer pageSize) {
        List<PlayerEntity> list = new ArrayList<>();
        playerRepo.findAll().iterator().forEachRemaining(list::add);
        list = sortList(list, order);

        list = getPlayerEntities(race, profession, banned, list);

        list = list.stream().filter(s -> s.getName().contains(name) &&
                s.getTitle().contains(title) &&
                s.getBirthday().getTime() >= after &&
                s.getBirthday().getTime() <= before &&
                s.getExperience() >= minExperience &&
                s.getExperience() <= maxExperience &&
                s.getLevel() >= minLevel &&
                s.getLevel() <= maxLevel).collect(Collectors.toList());
        int numberPlayers = pageNumber * pageSize;
        list = list.subList(Math.min(numberPlayers, list.size()), Math.min(numberPlayers + pageSize, list.size()));
        return list;
    }

    private List<PlayerEntity> getPlayerEntities(Race race, Profession profession, Boolean banned, List<PlayerEntity> list) {
        if (race != null)
            list = list.stream().filter(s -> s.getRace().equals(race)).collect(Collectors.toList());
        if (profession != null)
            list = list.stream().filter(s -> s.getProfession().equals(profession)).collect(Collectors.toList());
        if (banned != null)
            list = list.stream().filter(s -> s.getBanned() == banned).collect(Collectors.toList());
        return list;
    }

    public Object getPlayersCount(String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel) {
        List<PlayerEntity> list = new ArrayList<>();
        playerRepo.findAll().iterator().forEachRemaining(list::add);
        list = getPlayerEntities(race, profession, banned, list);
        list = list.stream().filter(s -> s.getName().contains(name) &&
                s.getTitle().contains(title) &&
                s.getBirthday().getTime() >= after &&
                s.getBirthday().getTime() <= before &&
                s.getExperience() >= minExperience &&
                s.getExperience() <= maxExperience &&
                s.getLevel() >= minLevel &&
                s.getLevel() <= maxLevel).collect(Collectors.toList());
        return list.size();
    }

    public PlayerEntity savePlayer(NewPlayer player) throws Exception {

        if ((player.getName().isEmpty() || player.getName().length() > 12) ||
                (player.getTitle().isEmpty() || player.getTitle().length() > 30) ||
                (player.getRace() == null) ||
                (player.getProfession() == null) ||
                (player.getBirthday() == null || player.getBirthday() < 0) ||
                (player.getExperience()) == null || player.getExperience() < 0 || player.getExperience() > 10_000_000)
            throw new Exception();

        PlayerEntity entity = new PlayerEntity();
        entity.setName(player.getName());
        entity.setTitle(player.getTitle());
        entity.setRace(player.getRace());
        entity.setProfession(player.getProfession());
        entity.setBirthday(new Date(player.getBirthday()));
        entity.setBanned(player.getBanned());
        entity.setExperience(player.getExperience());

        Integer level = (int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
        entity.setLevel(level);

        Integer untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();
        entity.setUntilNextLevel(untilNextLevel);

        return playerRepo.save(entity);
    }

    public PlayerEntity getPlayer(String id) throws PlayerNotFountException, IdNotValidException {
        Long value;
        PlayerEntity entity;
        try {
            value = Long.valueOf(id);
        } catch (Exception e) {
            throw new IdNotValidException();
        }
        if (id.equals("")) throw new IdNotValidException();
        if (value <= 0) throw new IdNotValidException();
        try {
            entity = playerRepo.findById(value).get();
        } catch (Exception e) {
            throw new PlayerNotFountException();
        }
        return entity;
    }

    public void deletePlayer(String id) throws PlayerNotFountException, IdNotValidException {
        PlayerEntity entity = getPlayer(id);
        playerRepo.delete(entity);
    }

    public PlayerEntity updatePlayer(UpdatePlayer player, String id) throws Exception {
        PlayerEntity entity = getPlayer(id);
        if ((!player.getName().isEmpty() && player.getName().length() > 12) ||
                (!player.getTitle().isEmpty() && player.getTitle().length() > 30) ||
                (player.getBirthday() != null && player.getBirthday() < 0) ||
                (player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10_000_000)))
            throw new Exception();

        if (!player.getName().isEmpty()) {
            entity.setName(player.getName());
        }
        if (!player.getTitle().isEmpty()) {
            entity.setTitle(player.getTitle());
        }
        if (player.getRace() != null) {
            entity.setRace(player.getRace());
        }
        if (player.getProfession() != null) {
            entity.setProfession(player.getProfession());
        }
        if (player.getBirthday() != null) {
            entity.setBirthday(new Date(player.getBirthday()));
        }
        if (player.getBanned() != null) {
            entity.setBanned(player.getBanned());
        }
        if (player.getExperience() != null) {
            entity.setExperience(player.getExperience());

            Integer level = (int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
            entity.setLevel(level);

            Integer untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();
            entity.setUntilNextLevel(untilNextLevel);
        }

        return playerRepo.save(entity);
    }
}
