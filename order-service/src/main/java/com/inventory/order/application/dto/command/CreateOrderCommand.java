package com.inventory.order.application.dto.command;

import java.util.List;

public class CreateOrderCommand {

    private List<CreateOrderItemCommand> items;

    public CreateOrderCommand() {
    }

    public CreateOrderCommand(List<CreateOrderItemCommand> items) {
        this.items = items;
    }

    public List<CreateOrderItemCommand> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemCommand> items) {
        this.items = items;
    }
}