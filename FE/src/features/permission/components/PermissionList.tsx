import type { PermissionResponse } from '../types/permission.types'

interface PermissionListProps {
  permissions: PermissionResponse[]
  isLoading: boolean
  onEdit: (permission: PermissionResponse) => void
  onDelete: (permission: PermissionResponse) => void
}

export function PermissionList({
  permissions,
  isLoading,
  onEdit,
  onDelete,
}: PermissionListProps) {
  if (isLoading) {
    return <p className="company-muted">Đang tải...</p>
  }

  if (permissions.length === 0) {
    return <p className="company-muted">Chưa có permission nào.</p>
  }

  return (
    <div className="company-table-wrap">
      <table className="company-table">
        <thead>
          <tr>
            <th>Tên</th>
            <th>Method</th>
            <th>API path</th>
            <th>Module</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {permissions.map((permission) => (
            <tr key={permission.id}>
              <td>{permission.name}</td>
              <td>{permission.method}</td>
              <td>{permission.apiPath}</td>
              <td>{permission.module}</td>
              <td className="company-actions">
                <button type="button" onClick={() => onEdit(permission)}>
                  Sửa
                </button>
                <button type="button" className="danger" onClick={() => onDelete(permission)}>
                  Xóa
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
