import { Component, inject, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant.service';
import { ConfirmOrder } from '../models';

@Component({
  selector: 'app-confirmation',
  standalone: false,
  templateUrl: './confirmation.component.html',
  styleUrl: './confirmation.component.css'
})
export class ConfirmationComponent implements OnInit {

  private restaurantSvc = inject(RestaurantService)

  protected confirmed!: ConfirmOrder

  // TODO: Task 5
  ngOnInit(): void {
    this.restaurantSvc.confirmedOrder
      .subscribe((resp) => {
        console.info('>>>', resp)
        this.confirmed = resp    
      })
  }

}
