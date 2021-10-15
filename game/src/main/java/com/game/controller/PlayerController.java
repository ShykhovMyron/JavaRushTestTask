package com.game.controller;

import com.game.entity.*;
import com.game.exeption.IdNotValidException;
import com.game.exeption.PlayerNotFountException;
import com.game.model.NewPlayer;
import com.game.model.UpdatePlayer;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/rest/players")
    public ResponseEntity getPlayers(@RequestParam(defaultValue = "") String name,
                                     @RequestParam(defaultValue = "") String title,
                                     @RequestParam(required = false) Race race,
                                     @RequestParam(required = false) Profession profession,
                                     @RequestParam(defaultValue = "0") Long after,
                                     @RequestParam(defaultValue = "9223372036854775807") Long before,
                                     @RequestParam(required = false) Boolean banned,
                                     @RequestParam(defaultValue = "0") Integer minExperience,
                                     @RequestParam(defaultValue = "2147483647") Integer maxExperience,
                                     @RequestParam(defaultValue = "0") Integer minLevel,
                                     @RequestParam(defaultValue = "2147483647") Integer maxLevel,
                                     @RequestParam(defaultValue = "ID") PlayerOrder order,
                                     @RequestParam(defaultValue = "0") Integer pageNumber,
                                     @RequestParam(defaultValue = "3") Integer pageSize) {
        try {
            return ResponseEntity.ok(playerService.getPlayers(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rest/players/count")
    public ResponseEntity getPlayerCount(@RequestParam(defaultValue = "") String name,
                                         @RequestParam(defaultValue = "") String title,
                                         @RequestParam(required = false) Race race,
                                         @RequestParam(required = false) Profession profession,
                                         @RequestParam(defaultValue = "0") Long after,
                                         @RequestParam(defaultValue = "9223372036854775807") Long before,
                                         @RequestParam(required = false) Boolean banned,
                                         @RequestParam(defaultValue = "0") Integer minExperience,
                                         @RequestParam(defaultValue = "2147483647") Integer maxExperience,
                                         @RequestParam(defaultValue = "0") Integer minLevel,
                                         @RequestParam(defaultValue = "2147483647") Integer maxLevel
    ) {
        try {
            return ResponseEntity.ok(playerService.getPlayersCount(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/rest/players")
    public ResponseEntity createPlayer(@RequestBody NewPlayer player) {
        try {
            PlayerEntity playerEntity = playerService.savePlayer(player);
            return ResponseEntity.ok(playerEntity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rest/players/{id}")
    public ResponseEntity getPlayer(@PathVariable String id) {
        try {
            return ResponseEntity.ok(playerService.getPlayer(id));
        } catch (IdNotValidException i) {
            return ResponseEntity.status(400).build();
        } catch (PlayerNotFountException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception f) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/rest/players/{id}")
    public ResponseEntity deletePlayer(@PathVariable String id) {
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.ok().build();
        } catch (IdNotValidException i) {
            return ResponseEntity.status(400).build();
        } catch (PlayerNotFountException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/rest/players/{id}")
    public ResponseEntity updatePlayer(@PathVariable String id,
                                       @RequestBody UpdatePlayer player) {
        try {
            playerService.updatePlayer(player,id);
            return ResponseEntity.ok().build();
        } catch (IdNotValidException i) {
            return ResponseEntity.status(400).build();
        } catch (PlayerNotFountException e) {
            return ResponseEntity.status(404).build();
        }catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }
}
