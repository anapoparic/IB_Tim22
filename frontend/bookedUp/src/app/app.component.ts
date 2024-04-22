import { Component } from '@angular/core';
import { WebSocketService } from './shared/notifications/service/web-socket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'bookedUp';
  isChecked: boolean = false;

  constructor(
    private webSocketService: WebSocketService) {
      this.webSocketService.connectToWebSocket();
    }

}
