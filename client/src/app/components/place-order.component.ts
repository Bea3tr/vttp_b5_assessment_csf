import { Component, inject, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant.service';
import { MenuItem } from '../models';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-place-order',
  standalone: false,
  templateUrl: './place-order.component.html',
  styleUrl: './place-order.component.css'
})
export class PlaceOrderComponent implements OnInit {

  // TODO: Task 3
  private restaurantSvc = inject(RestaurantService)
  private fb = inject(FormBuilder)
  private router = inject(Router)

  protected form !: FormGroup
  protected selectedItems: MenuItem[] = []
  protected total = 0

  ngOnInit(): void {
    this.form = this.createForm()
    this.restaurantSvc.placedOrders
      .subscribe((resp) => {
        console.info('>>>', resp)
        this.selectedItems = resp
      })
    this.restaurantSvc.total
      .subscribe((total) => {
        console.info('>>> Total:', total)
        this.total = total
      })
  }

  confirm() {
    console.info('>>> Confirming order')
    this.restaurantSvc.confirmOrders(this.form, this.selectedItems)
      .then((resp) => {
        console.info(resp)
        this.router.navigate(['/confirm'])
      })
      .catch((err) => {
        console.info(err)
        alert(err.message)
      })
  }

  discard() {
    this.router.navigate(['/'])
  }

  private createForm(): FormGroup {
    return this.fb.group({
      username: this.fb.control<string>(''),
      password: this.fb.control<string>('')
    }, { validators: this.atLeastOneInput })
  }

  private atLeastOneInput = (ctrl: AbstractControl) => {
    const values = Object.values(ctrl.value);
    const allEmpty = values.every(val => !val)
    if (!allEmpty) {
      return null
    }
    return { atLeastOneInput: true } as ValidationErrors
  }

}
