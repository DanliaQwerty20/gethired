package by.system.gethired.service.facade;


import by.system.gethired.controller.dto.TelegramUpdateDto;

public interface TelegramFacade {
    void process(TelegramUpdateDto update);
}