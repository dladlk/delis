import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DASHBOARD_PATH, DOCUMENT_PATH, IDENTIFIER_PATH, SEND_DOCUMENT_PATH } from './app.constants';
import { AuthGuard } from './guard/auth.guard';

import { NotFoundComponent } from './component/not-found/not-found.component';
import { LoginComponent } from './component/login/login.component';
import { LogoutComponent } from './component/logout/logout.component';
import { DashboardComponent } from './component/content/dashboard/dashboard.component';
import { LayoutComponent } from './component/layout/layout.component';
import { IdentifierComponent } from './component/content/identifier/identifier.component';
import { IdentifierDetailsComponent } from './component/content/identifier/identifier-details/identifier-details.component';
import { DocumentComponent } from './component/content/document/document.component';
import { DocumentDetailsComponent } from './component/content/document/document-details/document-details.component';
import { SendDocumentComponent } from './component/content/send-document/send-document.component';
import { SendDocumentDetailsComponent } from './component/content/send-document/send-document-details/send-document-details.component';

const routes: Routes = [
  {
    path: '', component: LayoutComponent, canActivateChild: [AuthGuard], children: [
      {
        path: '', redirectTo: '/' + DASHBOARD_PATH, pathMatch: 'full'
      },
      {
        path: DASHBOARD_PATH, component: DashboardComponent
      },
      {
        path: IDENTIFIER_PATH, component: IdentifierComponent
      },
      {
        path: IDENTIFIER_PATH + '/details', component: IdentifierDetailsComponent
      },
      {
        path: DOCUMENT_PATH, component: DocumentComponent
      },
      {
        path: DOCUMENT_PATH + '/details', component: DocumentDetailsComponent
      },
      {
        path: SEND_DOCUMENT_PATH, component: SendDocumentComponent
      },
      {
        path: SEND_DOCUMENT_PATH + '/details', component: SendDocumentDetailsComponent
      },
      {
        path: 'logout', component: LogoutComponent
      }
    ]
  },
  {
    path: 'login', component: LoginComponent
  },
  {
    path: 'not-found', component: NotFoundComponent
  },
  {
    path: '**', redirectTo: 'not-found'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
