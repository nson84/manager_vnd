import { useEffect, useState } from 'react'

import { AuthProvider, LoginPage, useAuth } from '../features/auth'
import { CashbookPage } from '../features/cashbook'
import { CompaniesPage, CompanyPickerPage } from '../features/company'
import type { PublicCompany } from '../features/company'
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
import { canAccess, defaultView, NAV_ITEMS, roleNames, type AppView } from './nav'

const SHOP_KEY = 'selectedShop'

function readShop(): PublicCompany | null {
  const raw = sessionStorage.getItem(SHOP_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as PublicCompany
  } catch {
    return null
  }
}

export function App() {
  return (
    <AuthProvider>
      <AppShell />
    </AuthProvider>
  )
}

function AppShell() {
  const { user, isReady, logout } = useAuth()
  const [shop, setShop] = useState<PublicCompany | null>(readShop)
  const roles = user ? roleNames(user.roles) : []
  const [view, setView] = useState<AppView>(() => defaultView(roles))

  useEffect(() => {
    if (shop) {
      sessionStorage.setItem(SHOP_KEY, JSON.stringify(shop))
      return
    }
    sessionStorage.removeItem(SHOP_KEY)
  }, [shop])

  useEffect(() => {
    if (user) {
      setView(defaultView(roleNames(user.roles)))
      if (!shop && user.company) {
        setShop({ id: user.company.id, name: user.company.name })
      }
    }
  }, [user, shop])

  if (!isReady) {
    return <p className="cashbook-page">Đang kiểm tra phiên...</p>
  }

  if (!user && !shop) {
    return <CompanyPickerPage onSelect={setShop} />
  }

  if (!user && shop) {
    return (
      <LoginPage
        companyId={shop.id}
        companyName={shop.name}
        onBack={() => setShop(null)}
      />
    )
  }

  const handleLogout = async () => {
    await logout()
    setShop(null)
  }

  return (
    <>
      <div className="cashbook-page" style={{ paddingBottom: 0, minHeight: 0 }}>
        <nav className="cashbook-nav" aria-label="Main">
          {shop && <span className="cashbook-shop">{shop.name}</span>}
          {NAV_ITEMS.filter((item) => canAccess(roles, item.roles)).map((item) => (
            <button
              key={item.view}
              type="button"
              className={view === item.view ? 'active' : ''}
              onClick={() => setView(item.view)}
            >
              {item.label}
            </button>
          ))}
          <button type="button" onClick={() => void handleLogout()}>
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
