package me.hwangje.smart_farm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hwangje.smart_farm.domain.Group;

public class GroupDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class AddGroupRequest{
        private String name;
        private String contact;
        private String registrationNumber;

        public Group toEntity(){
            return Group.builder()
                    .name(name)
                    .contact(contact)
                    .registrationNumber(registrationNumber)
                    .build();
        }
    }

    @Getter
    public static class GroupResponse{
        private String name;
        private String contact;
        private String registrationNumber;

        public GroupResponse(Group group){
            this.name = group.getName();
            this.contact = group.getContact();
            this.registrationNumber = group.getRegistrationNumber();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UpdateGroupRequest{
        private String name;
        private String contact;
        private String registrationNumber;
    }
}
