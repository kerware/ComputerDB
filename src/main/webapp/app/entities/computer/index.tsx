import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Computer from './computer';
import ComputerDetail from './computer-detail';
import ComputerUpdate from './computer-update';
import ComputerDeleteDialog from './computer-delete-dialog';

const ComputerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Computer />} />
    <Route path="new" element={<ComputerUpdate />} />
    <Route path=":id">
      <Route index element={<ComputerDetail />} />
      <Route path="edit" element={<ComputerUpdate />} />
      <Route path="delete" element={<ComputerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ComputerRoutes;
