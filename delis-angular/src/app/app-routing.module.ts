import { NgModule} from '@angular/core';
import { RouterModule, Routes} from '@angular/router';

import { AuthGuard } from './guard/auth.guard';
import { LayoutComponent } from './component/layout/layout.component';
import { LoginComponent } from './component/login/login.component';
import { DashboardComponent } from './component/content/dashboard/dashboard.component';
import { IdentifierComponent } from './component/content/identifier/identifier.component';
import { IdentifierDetailsComponent } from './component/content/identifier/identifier-details/identifier-details.component';
import { DocumentComponent } from './component/content/document/document.component';
import { DocumentDetailsComponent } from './component/content/document/document-details/document-details.component';
import { SendDocumentComponent } from './component/content/send-document/send-document.component';
import { SendDocumentDetailsComponent } from './component/content/send-document/send-document-details/send-document-details.component';
import { NotFoundComponent } from './component/not-found/not-found.component';
import { LogoutComponent } from './component/logout/logout.component';

const routes: Routes = [
  {
    path: '', component: LayoutComponent, canActivateChild: [AuthGuard], children: [
      {
        path: '', redirectTo: '/dashboard', pathMatch: 'full'
      },
      {
        path: 'dashboard', component: DashboardComponent
      },
      {
        path: 'identifier', component: IdentifierComponent
      },
      {
        path: 'identifier/:id', component: IdentifierDetailsComponent
      },
      {
        path: 'document', component: DocumentComponent
      },
      {
        path: 'document/:id', component: DocumentDetailsComponent
      },
      {
        path: 'send-document', component: SendDocumentComponent
      },
      {
        path: 'send-document/:id', component: SendDocumentDetailsComponent
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
