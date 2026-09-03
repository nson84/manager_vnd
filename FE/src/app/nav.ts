export type AppView =
  | 'users'
  | 'companies'
  | 'roles'
  | 'permissions'
  | 'customers'
  | 'workers'
  | 'wages'
  | 'debts'
  | 'expenses'
  | 'payslips'
  | 'cashbook'

export interface NavItem {
  view: AppView
  label: string
  roles: string[]
}

export const NAV_ITEMS: NavItem[] = [
  { view: 'users', label: 'Users', roles: ['ADMIN'] },
  { view: 'companies', label: 'Công ty', roles: ['ADMIN'] },
  { view: 'roles', label: 'Role', roles: ['ADMIN'] },
  { view: 'permissions', label: 'Permission', roles: ['ADMIN'] },
  { view: 'customers', label: 'Khách hàng', roles: ['ADMIN', 'MANAGER', 'STAFF', 'USER'] },
  { view: 'workers', label: 'Thợ', roles: ['ADMIN', 'MANAGER', 'STAFF', 'USER'] },
  { view: 'wages', label: 'Ghi công', roles: ['ADMIN', 'MANAGER', 'STAFF', 'USER'] },
  { view: 'debts', label: 'Công nợ', roles: ['ADMIN', 'MANAGER', 'STAFF', 'USER'] },
  { view: 'expenses', label: 'Phiếu chi', roles: ['ADMIN', 'MANAGER'] },
  { view: 'payslips', label: 'Phiếu lương', roles: ['ADMIN', 'MANAGER'] },
  { view: 'cashbook', label: 'Sổ quỹ', roles: ['ADMIN', 'MANAGER'] },
]

export function roleNames(roles: { name: string }[]): string[] {
  return roles.map((role) => role.name.toUpperCase())
}

export function canAccess(userRoles: string[], allowed: string[]): boolean {
  return allowed.some((role) => userRoles.includes(role))
}

export function defaultView(userRoles: string[]): AppView {
  const item = NAV_ITEMS.find((nav) => canAccess(userRoles, nav.roles))
  return item?.view ?? 'customers'
}
