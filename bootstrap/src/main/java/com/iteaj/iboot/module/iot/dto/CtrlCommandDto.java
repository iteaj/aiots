package com.iteaj.iboot.module.iot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CtrlCommandDto {

    private String apiName;

    private String attrField;

    private List<CommandItem> options;

    public CtrlCommandDto(String apiName) {
        this.apiName = apiName;
    }

    public void addOption(String commandName, String commandValue, Object value) {
        if(options == null) {
            options = new ArrayList<>();
        }

        options.add(new CommandItem(commandName, commandValue, value));
    }

    @Getter
    public static class CommandItem {

        private Object value;

        private String commandName;

        private String commandValue;

        public CommandItem(String commandName, String commandValue, Object value) {
            this.commandName = commandName;
            this.commandValue = commandValue;
            this.value = value;
        }
    }
}
