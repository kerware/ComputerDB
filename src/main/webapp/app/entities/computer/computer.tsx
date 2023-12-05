import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './computer.reducer';

export const Computer = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const computerList = useAppSelector(state => state.computer.entities);
  const loading = useAppSelector(state => state.computer.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    } else {
      return order === ASC ? faSortUp : faSortDown;
    }
  };

  return (
    <div>
      <h2 id="computer-heading" data-cy="ComputerHeading">
        <Translate contentKey="computerDbApp.computer.home.title">Computers</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="computerDbApp.computer.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/computer/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="computerDbApp.computer.home.createLabel">Create new Computer</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {computerList && computerList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="computerDbApp.computer.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="computerDbApp.computer.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('introduced')}>
                  <Translate contentKey="computerDbApp.computer.introduced">Introduced</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('introduced')} />
                </th>
                <th className="hand" onClick={sort('removed')}>
                  <Translate contentKey="computerDbApp.computer.removed">Removed</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('removed')} />
                </th>
                <th className="hand" onClick={sort('hardware')}>
                  <Translate contentKey="computerDbApp.computer.hardware">Hardware</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('hardware')} />
                </th>
                <th className="hand" onClick={sort('software')}>
                  <Translate contentKey="computerDbApp.computer.software">Software</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('software')} />
                </th>
                <th>
                  <Translate contentKey="computerDbApp.computer.company">Company</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {computerList.map((computer, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/computer/${computer.id}`} color="link" size="sm">
                      {computer.id}
                    </Button>
                  </td>
                  <td>{computer.name}</td>
                  <td>
                    {computer.introduced ? <TextFormat type="date" value={computer.introduced} format={APP_LOCAL_DATE_FORMAT} /> : null}
                  </td>
                  <td>{computer.removed ? <TextFormat type="date" value={computer.removed} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{computer.hardware}</td>
                  <td>{computer.software}</td>
                  <td>{computer.company ? <Link to={`/company/${computer.company.id}`}>{computer.company.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/computer/${computer.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/computer/${computer.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (location.href = `/computer/${computer.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="computerDbApp.computer.home.notFound">No Computers found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Computer;
