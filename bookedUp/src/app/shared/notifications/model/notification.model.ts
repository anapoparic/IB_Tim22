import { User } from "src/app/user/model/user.model";
import { NotificationType } from "./enum/notificationType.enum";

export interface Notification {
    id?: number;
    fromUserDTO: User;
    toUserDTO: User;
    title: string;
    message: string;
    timestamp: Date;
    type: NotificationType;
    active: boolean;

}