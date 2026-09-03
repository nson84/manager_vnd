import type { RoleResponse } from '../types/role.types'
import { isSystemRole } from '../types/role.types'

interface RoleListProps {
  roles: RoleResponse[]
  isLoading: boolean
  onEdit: (role: RoleResponse) => void
  onDelete: (role: RoleResponse) => void
}

export function RoleList({ roles, isLoading, onEdit, onDelete }: RoleListProps) {
  if (isLoading) {
    return <p className="company-muted">Đang tải...</p>
  }

  if (roles.length === 0) {
    return <p className="company-muted">Chưa có role nào.</p>
  }

  return (
    <div className="company-table-wrap">
      <table className="company-table">
        <thead>
          <tr>
            <th>Tên</th>
            <th>Mô tả</th>
            <th>Permissions</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {roles.map((role) => (
            <tr key={role.id}>
              <td>{role.name}</td>
              <td>{role.description || '—'}</td>
              <td>{role.permissions.length}</td>
              <td className="company-actions">
                <button type="button" onClick={() => onEdit(role)}>
                  Sửa
                </button>
                {!isSystemRole(role.name) && (
                  <button type="button" className="danger" onClick={() => onDelete(role)}>
                    Xóa
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
