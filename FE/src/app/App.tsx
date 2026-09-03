import { useState } from 'react'

import { AuthProvider, LoginPage, useAuth } from '../features/auth'
import { CashbookPage } from '../features/cashbook'
import { CompaniesPage } from '../features/company'
import { CustomersPage } from '../features/customer'
import { DebtsPage } from '../features/debt'
import { ExpensesPage } from '../features/expense'
import { PayslipsPage } from '../features/payslip'
import { PermissionsPage } from '../features/permission'
import { RolesPage } from '../features/role'
import { UsersPage } from '../features/user'
import { WagesPage } from '../features/wage'
import { WorkersPage } from '../features/worker'
import '../features/cashbook/components/cashbook.css'

type View =
  | 'users'
  | 'companies'
  | 'roles'
  | 'permissions'
  | 'customers'
  | 'cashbook'
  | 'workers'
  | 'wages'
  | 'debts'
  | 'expenses'
  | 'payslips'

export function App() {
  return (
    <AuthProvider>
      <AppShell />
    </AuthProvider>
  )
}

function AppShell() {
  const { user, isReady, logout } = useAuth()
  const [view, setView] = useState<View>('customers')

  if (!isReady) {
    return <p className="cashbook-page">Đang kiểm tra phiên...</p>
  }

  if (!user) {
    return <LoginPage />
  }

  return (
    <>
      <div className="cashbook-page" style={{ paddingBottom: 0, minHeight: 0 }}>
        <nav className="cashbook-nav" aria-label="Main">
          <button
            type="button"
            className={view === 'users' ? 'active' : ''}
            onClick={() => setView('users')}
          >
            Users
          </button>
          <button
            type="button"
            className={view === 'companies' ? 'active' : ''}
            onClick={() => setView('companies')}
          >
            Công ty
          </button>
          <button
            type="button"
            className={view === 'roles' ? 'active' : ''}
            onClick={() => setView('roles')}
          >
            Role
          </button>
          <button
            type="button"
            className={view === 'permissions' ? 'active' : ''}
            onClick={() => setView('permissions')}
          >
            Permission
          </button>
          <button
            type="button"
            className={view === 'customers' ? 'active' : ''}
            onClick={() => setView('customers')}
          >
            Khách hàng
          </button>
          <button
            type="button"
            className={view === 'workers' ? 'active' : ''}
            onClick={() => setView('workers')}
          >
            Thợ
          </button>
          <button
            type="button"
            className={view === 'wages' ? 'active' : ''}
            onClick={() => setView('wages')}
          >
            Ghi công
          </button>
          <button
            type="button"
            className={view === 'debts' ? 'active' : ''}
            onClick={() => setView('debts')}
          >
            Công nợ
          </button>
          <button
            type="button"
            className={view === 'expenses' ? 'active' : ''}
            onClick={() => setView('expenses')}
          >
            Phiếu chi
          </button>
          <button
            type="button"
            className={view === 'payslips' ? 'active' : ''}
            onClick={() => setView('payslips')}
          >
            Phiếu lương
          </button>
          <button
            type="button"
            className={view === 'cashbook' ? 'active' : ''}
            onClick={() => setView('cashbook')}
          >
            Sổ quỹ
          </button>
          <button type="button" onClick={() => void logout()}>
            Đăng xuất
          </button>
        </nav>
      </div>
      {view === 'users' && <UsersPage />}
      {view === 'companies' && <CompaniesPage />}
      {view === 'roles' && <RolesPage />}
      {view === 'permissions' && <PermissionsPage />}
      {view === 'customers' && <CustomersPage />}
      {view === 'workers' && <WorkersPage />}
      {view === 'wages' && <WagesPage />}
      {view === 'debts' && <DebtsPage />}
      {view === 'expenses' && <ExpensesPage />}
      {view === 'payslips' && <PayslipsPage />}
      {view === 'cashbook' && <CashbookPage />}
    </>
  )
}
