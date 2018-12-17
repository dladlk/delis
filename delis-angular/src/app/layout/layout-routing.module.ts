import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LayoutComponent } from './layout.component';

const routes: Routes = [
    {
        path: '',
        component: LayoutComponent,
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'prefix' },
            { path: 'dashboard', loadChildren: './dashboard/dashboard.module#DashboardModule' },
            { path: 'charts', loadChildren: './charts/charts.module#ChartsModule' },
            { path: 'tables', loadChildren: './tables/tables.module#TablesModule' },
            { path: 'documents', loadChildren: './documents/modules/documents.module#DocumentsModule' },
            { path: 'errors-invoices', loadChildren: './errors/components/invoices/errors-invoices.module#ErrorsInvoicesModule' },
            { path: 'errors-bis3-ubl', loadChildren: './errors/components/bis3-ubl/errors-bis3-ubl.module#ErrorsBis3UblModule' },
            { path: 'errors-cii', loadChildren: './errors/components/cii/errors-cii.module#ErrorsCiiModule' },
            { path: 'publishing', loadChildren: './publishing/publishing.module#PublishingModule' },
            { path: 'forms', loadChildren: './form/form.module#FormModule' },
            { path: 'bs-element', loadChildren: './bs-element/bs-element.module#BsElementModule' },
            { path: 'grid', loadChildren: './grid/grid.module#GridModule' },
            { path: 'components', loadChildren: './bs-component/bs-component.module#BsComponentModule' },
            { path: 'blank-page', loadChildren: './blank-page/blank-page.module#BlankPageModule' }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class LayoutRoutingModule {}
