import { Component, inject, OnInit } from '@angular/core';
import { MenuItem } from '../models';
import { RestaurantService } from '../restaurant.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {

  // TODO: Task 2
  private restaurantSvc = inject(RestaurantService)
  private router = inject(Router)

  protected menu: MenuItem[] = []
  protected numOrders = 0
  protected total = 0

  ngOnInit(): void {
    this.restaurantSvc.getMenuItems()
      .then((resp) => {
        console.info('Retrieving menu items')
        this.menu = resp
        this.menu.forEach((item) => {
          item.quantity = 0
        })
      })
  }

  addItem(item: MenuItem) {
    item.quantity += 1
    this.numOrders += 1
    this.total += item.price
  }

  removeItem(item: MenuItem) {
    if(item.quantity > 0){
      this.numOrders -= 1
      this.total -= item.price
      item.quantity -= 1
    }
  }

  placeOrders() {
    let selectedItems: MenuItem[] = []
    this.menu.forEach((item) => {
      if(item.quantity > 0) {
        selectedItems = [ ...selectedItems, item ]
      }
    })
    this.restaurantSvc.sendPlacedOrders(selectedItems, this.total);
    this.router.navigate(['/orders'])
  }

}
