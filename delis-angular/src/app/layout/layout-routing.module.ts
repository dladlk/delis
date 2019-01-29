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
            { path: 'documents', loadChildren: './content/documents/modules/documents.module#DocumentsModule' },
            { path: 'identifiers', loadChildren: './content/identifier/modules/identifier.module#IdentifierModule' },
            { path: 'journal-documents', loadChildren: './content/journal/document/modules/journal.document.module#JournalDocumentModule' },
            { path: 'journal-organisations', loadChildren: './content/journal/organisation/modules/journal.organisation.module#JournalOrganisationModule' },
            { path: 'journal-identifiers', loadChildren: './content/journal/identifier/modules/journal.identifier.module#JournalIdentifierModule' },
            { path: 'errors-invoices', loadChildren: './content/errors/components/invoices/errors-invoices.module#ErrorsInvoicesModule' },
            { path: 'errors-bis3-ubl', loadChildren: './content/errors/components/bis3-ubl/errors-bis3-ubl.module#ErrorsBis3UblModule' },
            { path: 'errors-cii', loadChildren: './content/errors/components/cii/errors-cii.module#ErrorsCiiModule' },
            { path: 'publishing', loadChildren: './content/publishing/publishing.module#PublishingModule' },
            { path: 'forms', loadChildren: './form/form.module#FormModule' },
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
