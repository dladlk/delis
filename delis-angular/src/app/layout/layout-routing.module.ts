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
            { path: 'documents', loadChildren: './content/documents/modules/documents.module#DocumentsModule' },
            { path: 'send-documents', loadChildren: './content/send/module/send.documents.module#SendDocumentsModule' },
            { path: 'identifiers', loadChildren: './content/identifier/modules/identifier.module#IdentifierModule' },
            { path: 'journal-documents', loadChildren: './content/journal/document/modules/journal.document.module#JournalDocumentModule' },
            { path: 'journal-organisations', loadChildren: './content/journal/organisation/modules/journal.organisation.module#JournalOrganisationModule' },
            { path: 'journal-identifiers', loadChildren: './content/journal/identifier/modules/journal.identifier.module#JournalIdentifierModule' },
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class LayoutRoutingModule {}
