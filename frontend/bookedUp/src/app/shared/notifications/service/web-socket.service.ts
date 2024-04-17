import { Injectable } from '@angular/core';
import { Notification } from '../model/notification.model';

import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { HttpClient } from '@angular/common/http';
import { Observable, map, of } from 'rxjs';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { NotificationType } from '../model/enum/notificationType.enum';
import { HostService } from 'src/app/user/host/host.service';
import { GuestService } from 'src/app/user/guest/guest.service';
import { Guest } from 'src/app/user/model/guest.model';
import { Host } from 'src/app/user/model/host.model';
import { Role } from 'src/app/user/model/role.enum';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  
  private stompClient: Stomp.Client | undefined;
  messages: Number[] = [];

  constructor(private http: HttpClient,
    private authService: AuthService,
    private hostService: HostService,
    private guestService: GuestService) {
  }

  connectToWebSocket(): void {
    const socket = new SockJS('http://localhost:8080/socket');
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect({}, (frame) => {
      console.log('Connected: ' + frame);
      if (this.stompClient !== undefined) {
        this.stompClient.subscribe('/topic/messages', (message) => {
          this.handleWebSocketMessage(message);
        });
      }
    }, (error) => {
      console.log('Error: ' + error);
    });
  }

  private handleWebSocketMessage(message: Stomp.Message): void {
    console.log('Received message: ' + message.body);

    const result = message.body.split(' ');

    if(result != undefined){
      this.messages.push(Number(result[0]));
    }
  }

  hasNotificationOnSocket(id: number): boolean {
    return this.messages.includes(id);
  }

  sendMessageUsingSocket(notification: Notification) {
    const socket = new SockJS('http://localhost:8080/socket');
    this.stompClient = Stomp.over(socket);
  
    this.stompClient.connect({}, (frame) => {
      if(notification.toUserDTO.role === Role.Guest){
        if(notification.toUserDTO.id !== undefined){
          this.guestService.getGuestById(notification.toUserDTO.id).subscribe(
            (guest: Guest) => {
              if(guest.notificationEnable){
                const message = notification.toUserDTO.id + ' ' + notification.type;
                  if((this.stompClient !== undefined) && (message !== undefined)){
                    this.stompClient.send('/app/send/message', {}, message.toString());
                  }
              }
            },
            (error) => {
              console.error('Error loading user:', error);
            }
          );
        }
      }else if(notification.toUserDTO.role === Role.Host){
        if(notification.toUserDTO.id !== undefined){
          this.hostService.getHost(notification.toUserDTO.id).subscribe(
            (host: Host) => {
              if((host.accommodationRatingNotificationEnabled) && (notification.type === NotificationType.accommodationRated)){
                const message = notification.toUserDTO.id + ' ' + notification.type;
                  if((this.stompClient !== undefined) && (message !== undefined)){
                    this.stompClient.send('/app/send/message', {}, message.toString());
                  }
              }
              if((host.hostRatingNotificationEnabled) && (notification.type === NotificationType.hostRated)){
                const message = notification.toUserDTO.id + ' ' + notification.type;
                  if((this.stompClient !== undefined) && (message !== undefined)){
                    this.stompClient.send('/app/send/message', {}, message.toString());
                  }
              }
              if((host.cancellationNotificationEnabled) && (notification.type === NotificationType.reservationCanceled)){
                const message = notification.toUserDTO.id + ' ' + notification.type;
                  if((this.stompClient !== undefined) && (message !== undefined)){
                    this.stompClient.send('/app/send/message', {}, message.toString());
                  }
              }
              if((host.reservationCreatedNotificationEnabled) && (notification.type === NotificationType.reservationCreated)){
                const message = notification.toUserDTO.id + ' ' + notification.type;
                  if((this.stompClient !== undefined) && (message !== undefined)){
                    this.stompClient.send('/app/send/message', {}, message.toString());
                  }
              }
            },
            (error) => {
              console.error('Error loading user:', error);
            }
          );
        }
      }
    }, (error) => {
      console.log('Error: ' + error);
    });
  }

  disconnectFromWebSocket(): void {
    if (this.stompClient) {
      this.stompClient.disconnect(() => {
        console.log('Disconnected');
      });
    }
  }
}
